package civictech.auth

import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class DefaultReactiveUserDetailsService(
    private val userRepository: UserRepository,
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String?): Mono<UserDetails> = mono {
        if (username == null)
            throw BadCredentialsException("Username cannot be null")

        val user =
            userRepository.findByName(username) ?: throw UsernameNotFoundException("User '$username' not found")

        val authorities = user.roles
            .map { "ROLE_$it" }
            .map(::SimpleGrantedAuthority)

        User(user.name, user.password, authorities)
    }
}