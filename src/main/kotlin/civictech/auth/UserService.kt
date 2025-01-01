package civictech.auth

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider
) {
    suspend fun registerUser(username: String, password: String, roles: List<String> = listOf("USER")): String {
        userRepository.findByName(username)?.run { throw UserExistsException(username) }

        val encodedPassword = passwordEncoder.encode(password)
        val user = UserDocument(
            version = null,
            name = username,
            password = encodedPassword,
            roles = roles
        )
        userRepository.save(user)

        return tokenProvider.generate(username, user.roles)
    }

    companion object {
        data class UserExistsException(val username: String) :
            RuntimeException("$username cannot be registered, as it already exists")
    }
}