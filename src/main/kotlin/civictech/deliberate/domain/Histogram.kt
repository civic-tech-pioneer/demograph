package civictech.deliberate.domain

data class Histogram(val buckets: List<Bucket>) {
    val centers: List<Degree> by lazy {
        buckets.map(Bucket::center)
    }
    val fractions: List<Degree> by lazy {
        buckets.map(Bucket::fraction)
    }
}