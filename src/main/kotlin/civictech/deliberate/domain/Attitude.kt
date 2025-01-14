package civictech.deliberate.domain

import java.util.*

data class Attitude(
    val agentName: String,
    val target: UUID,
    val attitude: Histogram
)