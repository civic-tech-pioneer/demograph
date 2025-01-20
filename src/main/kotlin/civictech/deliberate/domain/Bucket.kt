package civictech.deliberate.domain

import civictech.deliberate.domain.Degree.Companion.toDegree

data class Bucket(val left: Degree, val right: Degree, val value: Degree) {
    companion object {
        fun of(bucketCount: Int, index: Int, value: Degree): Bucket = Bucket(
            (index.toDouble() / bucketCount).toDegree(),
            ((index.toDouble() + 1) / bucketCount).toDegree(),
            value
        )
    }
}