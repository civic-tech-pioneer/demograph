package civictech.test

import com.netflix.graphql.dgs.client.GraphQLClient
import com.netflix.graphql.dgs.client.RestClientGraphQLClient
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Lazy
import org.springframework.core.env.Environment
import org.springframework.web.client.RestClient

@TestConfiguration
class DgsGraphQlClientTestConfig {
    @Bean
    @Lazy
    fun dgsGraphQlClient(environment: Environment): GraphQLClient = RestClientGraphQLClient(
        RestClient.create("http://localhost:${environment.getProperty("local.server.port")}/graphql")
    )
}