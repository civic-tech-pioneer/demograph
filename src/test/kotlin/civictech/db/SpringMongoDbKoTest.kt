package civictech.db

import civictech.test.MongoIntegrationTestConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.style.StringSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ChangeStreamEvent
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Flux
import reactor.test.StepVerifier


@Testcontainers
@ContextConfiguration(classes = [MongoIntegrationTestConfiguration::class])
class SpringMongoDbKoTest : StringSpec() {

    @Autowired
    lateinit var mongo: ReactiveMongoOperations

    override fun extensions(): List<Extension> = listOf(SpringExtension)

    data class Person(val name: String, val age: Int)

    init {
        "CRUD mongo with Spring injection" {
            mongo
                .insert(Person("Joe", 34))
                .then(mongo.query(Person::class.java).matching(where("name").`is`("Joe")).first())
                .doOnNext(System.out::println)
                .block()
            mongo.dropCollection("person").block()
        }

        "Listening should also work" {

            mongo.createCollection("person").block()

            val changeStream: Flux<ChangeStreamEvent<Person>> = mongo.changeStream(Person::class.java)
                .watchCollection("person")
                .listen()

            val joe = Person("Joe", 34)
            val jane = Person("Jane", 29)

            StepVerifier.create(changeStream)
                .expectSubscription()
                .then {
                    mongo
                        .insert(listOf(joe, jane), "person")
                        .`as`(StepVerifier::create)
                        .expectNextCount(2)
                        .verifyComplete()
                }
                .expectNextMatches { it.body == joe }
                .expectNextMatches { it.body == jane }
                .thenCancel()
                .verify()
        }
    }
}