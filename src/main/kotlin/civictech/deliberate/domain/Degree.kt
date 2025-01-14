package civictech.deliberate.domain

/**
 * Represents a floating point number between 0 and 1, inclusive.
 */
@JvmInline
value class Degree internal constructor(val value: Float) {
    fun min(other: Degree): Degree = Degree(kotlin.math.min(value, other.value))
    fun max(other: Degree): Degree = Degree(kotlin.math.max(value, other.value))
    fun avg(other: Degree): Degree = Degree((value + other.value) / 2)
    operator fun times(other: Degree): Degree = Degree(value * other.value)
    operator fun plus(other: Degree): Degree = clamp(value + other.value)

    companion object {
        val ZERO = Degree(0f)
        val ONE = Degree(1f)

        fun of(value: Float): Degree =
            orNull(value) ?: throw IllegalArgumentException("value must be between 0 and 1 inclusive")

        fun of(value: Double): Degree =
            orNull(value) ?: throw IllegalArgumentException("value must be between 0 and 1 inclusive")

        fun orNull(value: Float): Degree? = if (value in 0f..1f) Degree(value) else null
        fun orNull(value: Double): Degree? = if (value in 0f..1f) Degree(value.toFloat()) else null

        fun clamp(value: Float): Degree = when {
            value < 0 -> ZERO
            value > 1 -> ONE
            else -> Degree(value)
        }

        fun average(degrees: Iterable<Degree>): Degree {
            var count = 0
            var total = 0f
            for (degree in degrees) {
                count++
                total += degree.value
            }
            return if (count == 0) ZERO else Degree(total / count)
        }
    }
}
