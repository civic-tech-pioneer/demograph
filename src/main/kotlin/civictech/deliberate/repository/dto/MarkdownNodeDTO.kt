package civictech.deliberate.repository.dto

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Document(collection = "nodes")
@Table("elements")
data class MarkdownNodeDTO(
    @Id val id: UUID = UUID.randomUUID(),
    @Version val version: Int? = null,
    val text: String
)