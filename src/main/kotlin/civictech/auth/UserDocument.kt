package civictech.auth

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class UserDocument(
    @Id val id: String? = null,
    val username: String,
    val password: String,
    val roles: List<String> = listOf("USER")
)