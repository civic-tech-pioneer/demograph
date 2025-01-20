package civictech.deliberate.domain

import civictech.deliberate.domain.Degree.Companion.toDegree
import io.kotest.matchers.comparables.beGreaterThan
import io.kotest.matchers.comparables.beLessThanOrEqualTo
import io.kotest.matchers.should
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class HistogramTest {

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
}