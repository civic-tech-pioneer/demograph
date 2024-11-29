package civictech

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
@AutoConfiguration
class Main

fun main(args: Array<String>) {
    SpringApplicationBuilder(Main::class.java)
        .web(WebApplicationType.REACTIVE)
        .run(*args)
}