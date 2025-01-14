package civictech.auth

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.relational.core.mapping.Table

@Document(collection = "users")
@Table("users")
data class UserDocument(
    @Id val name: String,
    @Version val version: Int? = null,
    val password: String,
    val roles: List<String> = listOf("USER")
)