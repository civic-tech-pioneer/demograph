package civictech.auth

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI

@Configuration
@EnableConfigurationProperties(JwtConfig::class)
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtConfig: JwtConfig
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun reactiveJwtDecoder(): ReactiveJwtDecoder =
        NimbusReactiveJwtDecoder.withSecretKey(jwtConfig.secretKeySpec).build()

    @Bean
    fun jwtAuthenticationConverter(): ReactiveJwtAuthenticationConverter = ReactiveJwtAuthenticationConverter().apply {
        setJwtGrantedAuthoritiesConverter {
            Flux.fromStream(it.getClaimAsStringList("roles").stream().map(::SimpleGrantedAuthority))
        }
    }

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http {
            csrf { disable() }
            authorizeExchange {
                // Not centrally managed
                authorize(anyExchange, permitAll)
            }
            httpBasic {}
            formLogin {
                loginPage = "/auth/login.html"
                authenticationSuccessHandler = ServerAuthenticationSuccessHandler { webFilterExchange, _ ->
                    redirect(webFilterExchange, "/auth/login-success.html")
                }
                authenticationFailureHandler = ServerAuthenticationFailureHandler { webFilterExchange, _ ->
                    redirect(webFilterExchange, "/auth/login-failed.html")
                }
            }
            oauth2ResourceServer {
                jwt { }
            }
        }
    }


    private fun redirect(webFilterExchange: WebFilterExchange, page: String): Mono<Void> {
        webFilterExchange.exchange.apply {
            response.statusCode = HttpStatus.FOUND
            response.headers.location = URI.create(page) // Redirect on success
        }
        return Mono.empty()
    }
}