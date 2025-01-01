package civictech.auth

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Document(collection = "users")
@Table("users")
data class UserDocument(
    @Id val id: UUID = UUID.randomUUID(),
    @Version val version: Int?,
    val name: String,
    val password: String,
    val roles: List<String> = listOf("USER")
)