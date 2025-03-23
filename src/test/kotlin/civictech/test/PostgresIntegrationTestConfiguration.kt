package civictech.test

import civictech.deliberate.repository.convert.ConversionConfig
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@TestConfiguration
@Testcontainers
@Import(ConversionConfig::class)
class PostgresIntegrationTestConfiguration {

//    @Bean
//    fun postgresConnectionFactory(): ConnectionFactory =
//        ConnectionFactories.get("r2dbc:tc:postgresql:///deliberation?TC_IMAGE_TAG=17-alpine")

    @Bean
    @ServiceConnection
    fun postgresServiceConnection(): PostgreSQLContainer<*> = postgresContainer

    companion object {
        @JvmStatic
        @Container
        val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
            .withUsername("root")
            .withPassword("example")
            .withDatabaseName("deliberation")
            .apply { start() }
    }
}