package civictech.auth

import civictech.test.MongoIntegrationTestConfiguration
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        MongoReactiveUserDetailsService::class,
        MongoIntegrationTestConfiguration::class,
        UserRepository::class
    ]
)
@EnableMongoRepositories
@EnableAutoConfiguration
class MongoReactiveUserDetailsServiceTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var service: MongoReactiveUserDetailsService

    @Test
    fun userCanBeRetrieved() = runTest {
        userRepository.save(
            UserDocument(
                username = "username",
                password = "password",
                roles = listOf("USER")
            )
        )

        val user = service.findByUsername("username").block()

        user shouldNotBe null
        user?.username shouldBe "username"
        user?.password shouldBe "password"
        user?.authorities?.map { it.authority }?.shouldContainAll(listOf("ROLE_USER"))
    }
}