package civictech.auth

import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class TokenProvider(
    private val jwtConfig: JwtConfig
) : JwtTokenProvider {

    override fun generate(userName: String, roles: List<String>): String = Jwts.builder()
        .claims(mapOf("roles" to roles))
        .subject(userName)
        .issuedAt(Date())
        .expiration(Date.from(Instant.now().plus(jwtConfig.expiration)))
        .signWith(jwtConfig.secretKeySpec)
        .compact()
}