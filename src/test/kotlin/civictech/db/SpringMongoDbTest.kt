package civictech.db

import civictech.test.MongoIntegrationTestConfiguration
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.junit.jupiter.Testcontainers

@ExtendWith(SpringExtension::class)
@Testcontainers
@ContextConfiguration(classes = [MongoIntegrationTestConfiguration::class])
class SpringMongoDbTest {
    @Autowired
    lateinit var mongo: ReactiveMongoOperations

    data class Person(val name: String, val age: Int)

    @Test
    fun crudMongo() {
        mongo
            .insert(Person("Joe", 34))
            .then(mongo.query(Person::class.java).matching(where("name").`is`("Joe")).first())
            .doOnNext(::println)
            .block()
        mongo.dropCollection("person").block()
    }
}

