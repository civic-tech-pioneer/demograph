package civictech.auth

import civictech.test.DgsGraphQlClientTestConfig
import civictech.test.PostgresIntegrationTestConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.engine.runBlocking
import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.contain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    classes = [
        DgsGraphQlClientTestConfig::class,
        PostgresIntegrationTestConfiguration::class,
        RegistrationIntegrationTest.TestConfig::class,
    ]
)
@AutoConfigureWebTestClient
class RegistrationIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    data class RegisterRequest(
        val username: String,
        val password: String,
    )

    @BeforeEach
    fun setup() {
        runBlocking { userRepository.deleteAll() }
    }

    @Test
    fun `should register a new user successfully`() = runTest {
        val request = RegisterRequest(
            username = "testuser",
            password = "securePassword123",
        )

        val jsonRequest = objectMapper.writeValueAsString(request)

        val result = webTestClient.post()
            .uri("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonRequest)
            .exchange()
            .returnResult<String>()

        result.status shouldBe HttpStatus.CREATED

        val response = result.responseBody.blockLast()
        println("Registration Response: $response")

        // Assert the response contains a token
        response should contain("token")

        // Verify user is saved in MongoDB
        val savedUser = userRepository.findByName("testuser")

        savedUser shouldNotBe null
        savedUser?.name shouldBe "testuser"
        savedUser?.roles should containOnly("USER")
    }

    @Test
    fun `should fail to register an existing user`() {
        // First registration
        val request = RegisterRequest(
            username = "testuser",
            password = "securePassword123",
        )

        val jsonRequest = objectMapper.writeValueAsString(request)

        webTestClient.post()
            .uri("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonRequest)
            .exchange()
            .expectStatus().isCreated

        // Attempt to register the same user again
        val result = webTestClient.post()
            .uri("/auth")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonRequest)
            .exchange()
            .returnResult<String>()

        result.status shouldBe HttpStatus.CONFLICT
    }

    @Test
    fun `register via form`() = runTest {
        webTestClient.post()
            .uri("/auth")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("username=form-user&password=password")
            .exchange()
            .expectStatus().isFound
            .expectHeader().location("/auth/login.html")

        // Verify user is saved in MongoDB
        val savedUser = userRepository.findByName("form-user")

        savedUser shouldNotBe null
        savedUser?.name shouldBe "form-user"
        savedUser?.roles should containOnly("USER")
    }

    @Test
    fun `failed form registration`() = runTest {
        // first registration succeeds
        webTestClient.post()
            .uri("/auth")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("username=pre-existing-form-user&password=password")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().location("/auth/login.html")

        // second registration should fail
        webTestClient.post()
            .uri("/auth")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("username=pre-existing-form-user&password=password")
            .exchange()
            .expectStatus().isFound()
            .expectHeader().location("/auth/retry-registration.html")
    }

    @TestConfiguration
    @ComponentScan(basePackages = ["civictech.auth"])
    class TestConfig {
        @Bean
        fun objectMapper(): ObjectMapper = jacksonObjectMapper()
    }
}