package civictech.deliberate.domain

import kotlin.math.roundToInt

/**
 * Represents a floating point number in the unit interval, e.g. [0, 1]
 */
@JvmInline
value class Degree internal constructor(val value: Double) : Comparable<Degree> {
    fun min(other: Degree): Degree = Degree(kotlin.math.min(value, other.value))
    fun max(other: Degree): Degree = Degree(kotlin.math.max(value, other.value))
    fun avg(other: Degree): Degree = Degree((value + other.value) / 2)
    fun absDiff(other: Degree): Degree = Degree(kotlin.math.abs(value - other.value))
    operator fun times(other: Degree): Degree = Degree(value * other.value)
    operator fun div(other: Degree): Degree = Degree(value / other.value)
    operator fun plus(other: Degree): Degree = clamp(value + other.value)
    operator fun minus(other: Degree): Degree = clamp(value - other.value)

    override fun toString(): String {
        val rounded = (value * 100).roundToInt()
        val prefix = if (rounded.toDouble() != value * 100) "~" else ""
        return "$prefix$rounded%"
    }

    override fun compareTo(other: Degree): Int = value.compareTo(other.value)

    companion object {
        val ZERO = Degree(0.0)
        val ONE = Degree(1.0)

        fun of(value: Float): Degree =
            orNull(value) ?: throw IllegalArgumentException("value must be between 0 and 1 inclusive")

        fun of(value: Double): Degree =
            orNull(value) ?: throw IllegalArgumentException("value must be between 0 and 1 inclusive")

        fun orNull(value: Float): Degree? = if (value in 0f..1f) Degree(value.toDouble()) else null
        fun orNull(value: Double): Degree? = if (value in 0f..1f) Degree(value) else null

        fun clamp(value: Double): Degree = when {
            value < 0 -> ZERO
            value > 1 -> ONE
            else -> Degree(value)
        }

        @JvmName("averageOfDegree")
        fun Iterable<Degree>.average(): Degree =
            of(map(Degree::value).average())

        fun Int.toDegree(): Degree = of(this.toDouble())
        fun Double.toDegree(): Degree = of(this)
    }
}
