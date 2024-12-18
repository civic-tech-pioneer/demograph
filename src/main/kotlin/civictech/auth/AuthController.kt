package civictech.auth

import civictech.auth.UserService.Companion.UserExistsException
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import kotlinx.coroutines.reactor.mono
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI
import java.security.Principal

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val jwtTokenProvider: TokenProvider
) {

    data class RegisterRequest(
        @NotEmpty
        val username: String,
        @NotEmpty
        val password: String,
    )


    @PostMapping(consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    suspend fun registerForm(@Valid formRequest: RegisterRequest): ResponseEntity<Void> {
        userService.registerUser(formRequest.username, formRequest.password)
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI("/auth/login.html"))
            .build()
    }

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun registerBody(@RequestBody(required = false) @Valid bodyRequest: RegisterRequest): ResponseEntity<Map<String, String>> =
        ResponseEntity
            .status(HttpStatus.CREATED)
            .body(mapOf("token" to userService.registerUser(bodyRequest.username, bodyRequest.password)))


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    suspend fun token(principal: Principal): ResponseEntity<Map<String, String>> {
        if (principal is UsernamePasswordAuthenticationToken) {
            val token = jwtTokenProvider.generate(principal.name, principal.authorities.map { it.authority })
            return ResponseEntity.ok(mapOf("token" to token))
        }
        throw BadCredentialsException("Unsupported credentials to obtain a JWT token")
    }


    @ExceptionHandler(UserExistsException::class)
    fun handleUserAlreadyExistsException(
        exception: UserExistsException,
        request: ServerHttpRequest
    ): ResponseEntity<Void> {
        return if (request.headers.contentType == MediaType.APPLICATION_FORM_URLENCODED) {
            ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/auth/retry-registration.html"))
                .build()
        } else {
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        }
    }
}

