package civictech.deliberate.repository.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "link")
data class LinkDTO(
    @Id val id: UUID = UUID.randomUUID(),
    val sourceRef: UUID,
    val targetRef: UUID
)
