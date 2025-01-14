package civictech.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    suspend fun registerUser(username: String, password: String, roles: List<String> = listOf("USER")) {
        userRepository.findByName(username)?.run { throw UserExistsException(username) }

        val encodedPassword = passwordEncoder.encode(password)
        val user = UserDocument(
            name = username,
            password = encodedPassword,
            roles = roles
        )
        userRepository.save(user)
    }

    companion object {
        data class UserExistsException(val username: String) :
            RuntimeException("$username cannot be registered, as it already exists")
    }
}