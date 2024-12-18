package civictech.auth

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import java.time.Duration
import javax.crypto.spec.SecretKeySpec

@ConfigurationProperties(prefix = "auth.jwt")
data class JwtConfig @ConstructorBinding constructor(
    val secret: String,
    val expiration: Duration,
) {
    val secretKeySpec: SecretKeySpec by lazy {
        SecretKeySpec(secret.toByteArray(), "HmacSHA256")
    }
}