package civictech.auth

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : CoroutineCrudRepository<UserDocument, String> {
    suspend fun findByName(name: String): UserDocument?
}