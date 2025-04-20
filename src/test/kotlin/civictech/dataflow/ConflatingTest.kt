@file:OptIn(ExperimentalCoroutinesApi::class)

package civictech.dataflow

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConflatingTest {

    interface Mergeable<T> {
        fun mergeWith(other: T): T
    }

    fun <T> Flow<T>.conflating(): Flow<T> where T : Mergeable<T> = conflating { a, b, -> a.mergeWith(b) }

    fun <T : Any> Flow<T>.conflating(merger: (T, T) -> T): Flow<T> = flow {
        val channel = Channel<T>(capacity = 0)
        val mutex = Mutex()
        var buffering = false
        var buffer: T? = null

        suspend fun flushBuffer() {
            while (true) {
                val toSend = mutex.withLock {
                    val v = buffer
                    if (v == null) {
                        buffering = false
                        return
                    }
                    buffer = null
                    v
                }
                channel.send(toSend) // suspends if downstream is busy
            }
        }

        coroutineScope {
            val upstreamJob = launch {
                var flushJob: Job? = null

                collect { value ->
                    mutex.withLock {
                        if (buffering) {
                            // merge `value` into `buffer` for the next downstream propagation
                            buffer = buffer?.let { merger(it, value) } ?: value
                            return@collect
                        } else if (channel.trySend(value).isSuccess) {
                            // quick-path, no need to flush our local buffer
                            return@collect
                        }

                        // couldn't send, so switch to merging elements into our one-element buffer and
                        // propagating that in a separate coroutine
                        buffering = true
                        buffer = value
                        flushJob = launch { flushBuffer() }
                    }
                }

                // upstream terminated, but a flushing process might still be running
                // ensure we wait for a flushing process (if it exists) to finish
                flushJob?.join()
                // now it's safe to close our channel
                channel.close()
            }

            for (value in channel) {
                emit(value)
            }

            upstreamJob.cancel()
        }
    }


    data class Add(val value: Int) : Mergeable<Add> {
        operator fun plus(other: Add) = Add(this.value + other.value)
        override fun mergeWith(other: Add): Add = Add(value + other.value)
    }

    @Test
    fun `passes values immediately when collector is ready`() = runTest {
        val emitted = mutableListOf<Add>()

        val count = 100
        val flow = channelFlow {
            repeat(count) {
                send(Add(1))
                delay(1)
            }
        }
        flow
            .conflating()
            .onEach { emitted += it }
            .collect()

        assertEquals(count, emitted.size)
    }

    @Test
    fun `buffers one value if collector is slow`() = runTest {
        val emitted = mutableListOf<Add>()

        val upstream = flow {
            emit(Add(1))
            emit(Add(2))
            emit(Add(3))
        }

        upstream
            .conflating()
            .collect { value ->
                advanceTimeBy(10)
                emitted += value
            }

        assertEquals(listOf(Add(1), Add(5)), emitted)
    }

    @Test
    fun `merges multiple values into buffer when collector is backpressured`() = runTest {
        val emitted = mutableListOf<Add>()

        val upstream = flow {
            emit(Add(1))
            emit(Add(2))
            emit(Add(3))
            emit(Add(4))
        }

        upstream
            .conflating()
            .collect {
                delay(100) // block collector for a while
                emitted += it
            }

        // First value emitted, others merged while collector is busy
        assertEquals(listOf(Add(1), Add(9)), emitted)
    }

    @Test
    fun `handles concurrent emissions and merges deterministically`() = runTest {
        val emitted = mutableListOf<Add>()
        val count = 1000
        val flow = channelFlow {
            repeat(count) {
                launch {
                    send(Add(1))
                }
            }
        }

        flow
            .conflating()
            .collect {
                emitted += it
            }

        // 1 initial, rest should be merged
        val total = emitted.sumOf {
            println(it)
            it.value
        }
        assertEquals(count, total)
    }

    @Test
    fun `flushes buffered value when collector resumes`() = runTest {
        val emitted = mutableListOf<Add>()

        val flow = flow {
            emit(Add(10))
            emit(Add(20))
        }

        flow
            .conflating()
            .collect {
                emitted += it
                delay(50)
            }

        assertEquals(listOf(Add(10), Add(20)), emitted)
    }

    @Test
    fun `merges correctly when collector is suspended mid-burst`() = runTest {
        val emitted = mutableListOf<Add>()

        val flow = flow {
            emit(Add(1))
            delay(10)
            emit(Add(2))
            delay(10)
            emit(Add(3))
            delay(10)
            emit(Add(4))
        }

        flow
            .conflating()
            .collect {
                emitted += it
                delay(20) // simulate processing time
            }

        val total = emitted.sumOf { it.value }
        assertEquals(10, total) // 1 + 2 + 3 + 4
    }
}