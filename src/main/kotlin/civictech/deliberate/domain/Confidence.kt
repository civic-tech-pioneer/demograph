package civictech.deliberate.domain

import kotlin.math.pow

@JvmInline
value class Confidence internal constructor(val value: Double) {

    fun scaled(pow: Double = 0.1): Double = value.pow(pow)

    companion object {
        val NONE = Confidence(0.0)
        val FULL = Confidence(1.0)

        fun of(value: Float): Confidence =
            orNull(value) ?: throw IllegalArgumentException("value must be between 0 and 1 inclusive")
        fun of(value: Double): Confidence =
            orNull(value) ?: throw IllegalArgumentException("value must be between 0 and 1 inclusive")

        fun orNull(value: Float): Confidence? = if (value in 0f..1f) Confidence(value.toDouble()) else null
        fun orNull(value: Double): Confidence? = if (value in 0f..1f) Confidence(value) else null

        fun Float.toConfidence(): Confidence = of(this)
        fun Double.toConfidence(): Confidence = of(this)
    }
}
