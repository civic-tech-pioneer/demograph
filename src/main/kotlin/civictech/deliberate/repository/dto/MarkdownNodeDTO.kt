package civictech.deliberate.repository.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "nodes")
data class MarkdownNodeDTO(
    @Id val id: UUID = UUID.randomUUID(),
    val text: String
)