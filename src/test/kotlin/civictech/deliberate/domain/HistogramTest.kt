package civictech.deliberate.domain

import civictech.deliberate.domain.Histogram.Companion.approxEquals
import civictech.deliberate.domain.Histogram.Companion.maxAbsDifference
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class HistogramTest {

    @Test
    fun `maxAbsDifference is only defined for histograms with a same definition`() {
        val h1 = HistogramDef.of(1).populate { Degree.ZERO }
        val h2 = HistogramDef.of(2).populate { Degree.ZERO }

        assertThrows<RuntimeException> { h1.maxAbsDifference(h2) }
    }

    @Test
    fun `maxAbsDifference should return the maximum difference of all buckets`() {
        val h1 = HistogramDef.of(2).populate { Degree.of(0.2) }
        val h2 = HistogramDef.of(2).populate { Degree.of(0.3) }

        h1.maxAbsDifference(h2)?.value shouldBe (0.1 plusOrMinus 0.000001)
    }

    @Test
    fun `approxEquals should be reflexive`() {
        val histogram = HistogramDef.of(2).populate { Degree.of(0.2) }
        histogram.approxEquals(histogram).shouldBeTrue()
    }

    @Test
    fun `approxEquals should be true if their difference is within the tolerance`() {
        val h1 = HistogramDef.of(2).populate { Degree.of(0.5) }
        val h2 = HistogramDef.of(2).populate { Degree.of(0.6) }

        h1.approxEquals(h2, Degree.of(0.1)).shouldBeTrue()
    }

    @Test
    fun `approxEquals should be false if their difference exceeds the tolerance`() {
        val h1 = HistogramDef.of(2).populate { Degree.of(0.5) }
        val h2 = HistogramDef.of(2).populate { Degree.of(0.7) }

        h1.approxEquals(h2, Degree.of(0.1)).shouldBeFalse()
    }
}