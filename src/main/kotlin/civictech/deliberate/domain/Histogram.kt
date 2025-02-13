package civictech.deliberate.domain

interface Histogram {
    val buckets: List<Bucket>
    val def: HistogramDef

    fun rebin(histogramDef: HistogramDef): Histogram
}