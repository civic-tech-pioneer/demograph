package civictech.auth

import civictech.test.MongoIntegrationTestConfiguration
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.FluxExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    classes = [
        AuthController::class,
        SecurityConfig::class,
        SecurityIntegrationTest.AuthTestConfig::class,
        TokenProvider::class,
        UserService::class,
        UserRepository::class,
        MongoIntegrationTestConfiguration::class,
        MongoReactiveUserDetailsService::class
    ]
)
@AutoConfigureWebTestClient
@EnableAutoConfiguration
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecurityIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var userService: UserService

    @BeforeAll
    fun setup() {
        runBlocking {
            userService.registerUser("lowly", "password", listOf())
            userService.registerUser("admin", "password", listOf("ADMIN"))
        }
    }

    @Test
    fun `public endpoint can be accessed anonymously`() {
        webTestClient.get()
            .uri("/public")
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun `authenticated endpoint cannot be accessed anonymously`() {
        webTestClient.get()
            .uri("/authenticated")
            .exchange()
            .expectHeader().exists(HttpHeaders.WWW_AUTHENTICATE)
            .expectStatus().isUnauthorized()
    }

    @Test
    fun `authenticated endpoint can be accessed with basic authentication by anyone`() {
        webTestClient.get()
            .uri("/authenticated") // The endpoint requiring authentication
            .headers {
                it.setBasicAuth(
                    "lowly",
                    "password"
                )
            }
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun `authorized endpoint cannot be accessed by lowly user`() {
        webTestClient.get()
            .uri("/authorized")
            .headers {
                it.setBasicAuth(
                    "lowly",
                    "password"
                )
            }
            .exchange()
            .expectStatus().isForbidden()
    }

    @Test
    fun `authorized endpoint can be accessed by admin using basic authentication`() {
        webTestClient.get()
            .uri("/authorized")
            .headers {
                it.setBasicAuth(
                    "admin",
                    "password"
                )
            }
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun `should allow retrieving a JWT token using basic credentials`() {
        val response: FluxExchangeResult<Map<*, *>> = webTestClient.get()
            .uri("/auth")
            .headers {
                it.setBasicAuth(
                    "admin",
                    "password"
                )
            }
            .exchange()
            .expectStatus().isOk()
            .returnResult(Map::class.java)

        val body = response.responseBody.blockLast() as Map<String, String>
        val token = body["token"]

        token shouldNotBe null

        webTestClient.get()
            .uri("/authorized")
            .headers { it.setBearerAuth(token!!) }
            .exchange()
            .expectStatus().isOk()
    }

    @Test
    fun `form login sets cookie`() {
        webTestClient.post()
            .uri("/auth/login.html")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("username=lowly&password=password")
            .exchange()
            .expectStatus().isFound
            .expectCookie().httpOnly("SESSION", true)
            .expectHeader().location("/auth/login-success.html")

    }

    @Test
    fun `test invalid credentials return unauthorized`() {
        webTestClient.post()
            .uri("/auth/login.html")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("username=user&password=wrongpassword")
            .exchange()
            .expectStatus().isFound
            .expectCookie().doesNotExist("SESSION")
            .expectHeader().location("/auth/login-failed.html")
    }

    @TestConfiguration
    class AuthTestConfig {
        @RestController
        class Controller {

            @GetMapping("public")
            suspend fun public(): ResponseEntity<String> = ResponseEntity.ok("public")

            @GetMapping("authenticated")
            @PreAuthorize("isAuthenticated()")
            suspend fun authenticated(): ResponseEntity<String> = ResponseEntity.ok("authenticated")

            @GetMapping("authorized")
            @PreAuthorize("hasRole('ADMIN')")
            suspend fun authorized(): ResponseEntity<String> = ResponseEntity.ok("authorized")
        }
    }
}