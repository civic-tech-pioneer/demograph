package civictech

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
@AutoConfiguration
class Main

fun main(args: Array<String>) {
    SpringApplication.run(Main::class.java, *args)
}