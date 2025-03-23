package civictech.deliberate.domain

interface Histogram {
    val buckets: List<Bucket>
    val def: HistogramDef

    fun rebin(histogramDef: HistogramDef): Histogram

    companion object {
        private val TOLERANCE: Degree = Degree.of(0.0000001)

        fun Histogram.maxAbsDifference(other: Histogram): Degree? {
            require(def == other.def)
            return buckets
                .zip(other.buckets)
                .maxOfOrNull { (b1, b2) -> b1.value.absDiff(b2.value) }
        }

        fun Histogram.approxEquals(other: Histogram, tolerance: Degree = TOLERANCE): Boolean =
            (maxAbsDifference(other) ?: Degree.ZERO) <= tolerance
    }
}