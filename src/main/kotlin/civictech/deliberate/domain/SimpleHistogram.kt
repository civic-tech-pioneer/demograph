package civictech.deliberate.domain

import civictech.deliberate.domain.Degree.Companion.ZERO

data class SimpleHistogram private constructor(val histogramDef: HistogramDef, val bucketValues: List<Degree>) :
    Histogram {

    private fun slice(left: Degree, right: Degree): List<Bucket> =
        buckets.dropWhile { it.right < left }.takeWhile { it.left < right }

    override fun rebin(histogramDef: HistogramDef): Histogram =
        histogramDef.populate { ratio(it.left, it.right) }

    private fun ratio(left: Degree, right: Degree): Degree {
        fun bucketOverlap(bucketLeft: Degree, bucketRight: Degree): Degree {
            val bucketRange = bucketRight - bucketLeft
            return ZERO.max(right.min(bucketRight) - left.max(bucketLeft)) / bucketRange
        }

        return slice(left, right)
            .map { it.value * bucketOverlap(it.left, it.right) }
            .fold(ZERO, Degree::plus)
    }

    override val buckets: List<Bucket> by lazy {
        histogramDef.bucketDefs.zip(bucketValues.toList()).map {
            Bucket(it.first.left, it.first.right, it.second)
        }
    }

    val fractions: List<Degree> by lazy {
        buckets.map(Bucket::value)
    }


    companion object {

        // TODO: Implement properly at some point :)
        fun of(buckets: List<Bucket>): SimpleHistogram? {
            val histogramDef =
                HistogramDef.ofBoundaries((buckets.map { it.left } + buckets.map { it.right }).toSortedSet())
            val values = buckets.map { it.value }.toList()
            return if (histogramDef.bucketDefs.size == values.size) SimpleHistogram(histogramDef, values) else null
        }

        fun of(histogramDef: HistogramDef, bucketValues: List<Degree>): SimpleHistogram =
            SimpleHistogram(histogramDef, bucketValues)

//        fun arithmeticMean(histograms: Collection<Histogram>): Histogram? =
//            if (histograms.isEmpty()) null else arithmeticMean(histograms.head())

        fun arithmeticMean(first: Histogram, vararg others: Histogram): Histogram {
            return first // TODO
        }
    }
}