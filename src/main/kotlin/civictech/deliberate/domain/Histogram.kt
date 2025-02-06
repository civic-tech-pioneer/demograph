package civictech.deliberate.domain

interface Histogram {
    val buckets: List<Bucket>

    fun rebin(histogramDef: HistogramDef): Histogram
}