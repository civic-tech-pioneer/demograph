package civictech.test

import com.netflix.graphql.dgs.client.GraphQLClient
import com.netflix.graphql.dgs.client.RestClientGraphQLClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Fallback
import org.springframework.context.annotation.Lazy
import org.springframework.core.env.Environment
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient
import java.util.function.Consumer

@TestConfiguration
class DgsGraphQlClientTestConfig {

    // Define your own `Consumer<HttpHeaders>` bean if you want to customize the headers
    @Bean
    @Fallback
    fun headersConsumer(): Consumer<HttpHeaders> = Consumer {}

    @Bean
    @Lazy
    fun dgsGraphQlClient(environment: Environment, headersConsumer: Consumer<HttpHeaders>): GraphQLClient =
        RestClientGraphQLClient(
            RestClient.create("http://localhost:${environment.getProperty("local.server.port")}/graphql"),
            headersConsumer
        )
}