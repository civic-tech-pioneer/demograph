package civictech.deliberate.domain

import java.util.*

data class Link(
    val id: UUID,
    val sourceRef: UUID,
    val targetRef: UUID
) {
}