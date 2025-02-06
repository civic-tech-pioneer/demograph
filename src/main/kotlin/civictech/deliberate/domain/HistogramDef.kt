package civictech.deliberate.domain

import civictech.deliberate.domain.Degree.Companion.ONE
import civictech.deliberate.domain.Degree.Companion.ZERO
import org.apache.commons.statistics.distribution.BetaDistribution
import java.util.*
import kotlin.math.max
import kotlin.math.min

data class HistogramDef private constructor(val bucketDefs: List<BucketDef>) {

    fun populate(getDegree: (BucketDef) -> Degree): Histogram =
        SimpleHistogram.of(this, bucketDefs.map(getDegree))

    fun distribution(expected: Degree, confidence: Confidence, epsilon: Double = 1e-12): Histogram {
        // clamp c to (0,1) so we never get infinite alpha/beta
        val c = max(epsilon, min(1.0 - epsilon, confidence.scaled()))

        // Derive appropriate values for alpha and beta
        val alpha = 1.0 + (c / (1.0 - c)) * expected.value
        val beta = 1.0 + (c / (1.0 - c)) * (1.0 - expected.value)

        val dist = BetaDistribution.of(alpha, beta)

        // Calculate buckets
        return populate { bDef -> Degree.of(
            dist.cumulativeProbability(bDef.right.value) - dist.cumulativeProbability(bDef.left.value)
        )}
    }

    companion object {
        @JvmStatic
        val DEFAULT: HistogramDef = of(bucketCount = 9)

        @JvmStatic
        val EDGES: HistogramDef = ofBoundaries(
            listOf(0.0625, 0.1875, 0.3125, 0.4375, 0.5625, 0.6875, 0.8125, 0.9375)
                .map(Degree::of)
                .toSortedSet(Comparator.comparing { it.value })
        )

        fun of(bucketCount: Int): HistogramDef = HistogramDef((1..bucketCount).map {
            BucketDef(Degree.of((it - 1) / bucketCount.toDouble()), Degree.of(it / bucketCount.toDouble()))
        })

        fun ofBoundaries(boundaries: SortedSet<Degree>): HistogramDef {
            val allBoundaries: SortedSet<Degree> = (boundaries + ZERO + ONE)
                .toSortedSet(Comparator.comparing { it.value })

            val boundaryPairs = allBoundaries.zip(allBoundaries.tailSet(allBoundaries.drop(1).first()))
            return HistogramDef(boundaryPairs.map { BucketDef(it.first, it.second) })
        }

        data class BucketDef(val left: Degree, val right: Degree) {
            val center = if (left == ZERO && right != ONE) {
                ZERO
            } else if (right == ONE && left != ZERO) {
                ONE
            } else {
                left.avg(right)
            }
        }
    }

}