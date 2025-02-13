package civictech.deliberate.domain

import civictech.deliberate.domain.Confidence.Companion.toConfidence
import civictech.deliberate.domain.Degree.Companion.ONE
import civictech.deliberate.domain.Degree.Companion.ZERO
import civictech.deliberate.domain.Degree.Companion.toDegree
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beGreaterThanOrEqualTo
import io.kotest.matchers.comparables.beLessThanOrEqualTo
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.Comparator.comparingDouble

class SimpleHistogramTest {

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9])
    fun `higher confidence means a higher value close to the mean and lower in the extremes`(confidence: Double) {
        val lowerConfidence = HistogramDef.DEFAULT.distribution(0.5.toDegree(), Confidence.of(confidence))
        val higherConfidence = HistogramDef.DEFAULT.distribution(0.5.toDegree(), Confidence.of(confidence + 0.1))

        // Central value should be greater for higher confidence
        higherConfidence.buckets[4].value should beGreaterThan(lowerConfidence.buckets[4].value)

        // Extreme values should be smaller for higher confidence
        higherConfidence.buckets[0].value should beLessThanOrEqualTo(lowerConfidence.buckets[0].value)
        higherConfidence.buckets[8].value should beLessThanOrEqualTo(lowerConfidence.buckets[8].value)
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0])
    fun `minimal means are supported`(confidence: Double) {
        val minimal = HistogramDef.DEFAULT.distribution(Degree.ZERO, Confidence.of(confidence))
        minimal.buckets.slice(1 until 8).forEach { bucket: Bucket ->
            minimal.buckets[0].value should beGreaterThanOrEqualTo(bucket.value)
        }
    }

    @ParameterizedTest
    @ValueSource(doubles = [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0])
    fun `maximal means are supported`(confidence: Double) {
        val maximal = HistogramDef.DEFAULT.distribution(Degree.ONE, Confidence.of(confidence))
        maximal.buckets.slice(0 until 7).forEach { bucket: Bucket ->
            maximal.buckets[8].value should beGreaterThanOrEqualTo(bucket.value)
        }
    }

    @Test
    fun `rebinned normal distribution should keep its distribution`() {
        val manyBins = HistogramDef.of(bucketCount = 30).distribution(0.5.toDegree(), 0.5.toConfidence())
        val rebinned: Histogram = manyBins.rebin(HistogramDef.DEFAULT)

        rebinned.buckets shouldHaveSize 9
        rebinned.buckets.slice(0 until 5) shouldBeSortedWith comparingDouble { it.value.value }
        rebinned.buckets.slice(4 until 9) shouldBeSortedWith  comparingDouble<Bucket?> { it.value.value }.reversed()
    }

    @Test
    fun `single bin histogram should extrapolate to flat distribution`() {
        val singleBin = HistogramDef.of(bucketCount = 1).distribution(0.5.toDegree(), Confidence.FULL)
        val rebinned = singleBin.rebin(HistogramDef.DEFAULT)

        rebinned.buckets shouldHaveSize 9
        rebinned.buckets.forEach {
            it.value.value shouldBe (1.0 / 9 plusOrMinus 0.0000001)
        }
    }

    @Test
    fun `flat distribution should fill up a single bin histogram`() {
        val flatDistribution = HistogramDef.DEFAULT.distribution(0.5.toDegree(), Confidence.NONE)
        val rebinned = flatDistribution.rebin(HistogramDef.of(1))

        rebinned.buckets shouldHaveSize 1
        rebinned.buckets[0].value shouldBe ONE
    }

    @Test
    fun `arithmetic average of equivalent histograms is the same histogram`() {
        val normalDistribution = HistogramDef.DEFAULT.distribution(0.5.toDegree(), 0.5.toConfidence())
        val meanDistribution = SimpleHistogram.arithmeticMean(normalDistribution, normalDistribution, normalDistribution)

        meanDistribution.def shouldBe normalDistribution.def
        meanDistribution.buckets.zip(normalDistribution.buckets).forEach { (b1, b2) ->
            b1.value.value shouldBe (b2.value.value plusOrMinus 0.0000001)
        }
    }

    @Test
    fun `arithmetic mean of two same-binned histograms should be the point-wise average`() {
        val d1 = HistogramDef.DEFAULT.distribution((3.0 / 18).toDegree(), Confidence.FULL)
        val d2 = HistogramDef.DEFAULT.distribution((15.0 / 18).toDegree(), Confidence.FULL)

        val meanDistribution = SimpleHistogram.arithmeticMean(d1, d2)
        meanDistribution shouldBe SimpleHistogram.of(HistogramDef.DEFAULT, listOf(
            ZERO,
            0.5.toDegree(),
            ZERO,
            ZERO,
            ZERO,
            ZERO,
            ZERO,
            0.5.toDegree(),
            ZERO
        ))
    }
}