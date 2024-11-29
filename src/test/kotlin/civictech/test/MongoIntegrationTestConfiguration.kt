package civictech.test

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.UuidRepresentation
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.testcontainers.containers.MongoDBContainer

@TestConfiguration
class MongoIntegrationTestConfiguration {

    companion object {
        const val DEFAULT_MONGO_IMAGE_NAME = "mongo:8.0.3"
        const val DEFAULT_MONGO_HOST = "localhost"
        const val DEFAULT_MONGO_DATABASE_NAME = "test"
    }

    @Bean
    fun mongoDbTestContainer(): MongoDBContainer {
        return MongoDBContainer(DEFAULT_MONGO_IMAGE_NAME).apply { start() }
    }

    @Bean
    fun reactiveMongoTemplate(mongoDbTestContainer: MongoDBContainer): ReactiveMongoTemplate {
        val firstMappedPort = mongoDbTestContainer.firstMappedPort.toString()
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(mongoDbTestContainer.replicaSetUrl))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build()

        val client = MongoClients.create(settings)

        System.setProperty("spring.data.mongodb.host", DEFAULT_MONGO_HOST)
        System.setProperty("spring.data.mongodb.port", firstMappedPort)
        System.setProperty("spring.data.mongodb.database", DEFAULT_MONGO_DATABASE_NAME)
        System.setProperty("spring.data.mongodb.uri", mongoDbTestContainer.replicaSetUrl)

        return ReactiveMongoTemplate(client, DEFAULT_MONGO_DATABASE_NAME)
    }
}