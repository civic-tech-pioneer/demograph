package civictech

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication(
    exclude = [
        MongoReactiveRepositoriesAutoConfiguration::class
    ]
)
class Main

fun main(args: Array<String>) {
    SpringApplicationBuilder(Main::class.java)
        .web(WebApplicationType.REACTIVE)
        .run(*args)
}