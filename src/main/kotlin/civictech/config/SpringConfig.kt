package civictech.config

import civictech.deliberate.Deliberation
import civictech.deliberate.DeliberationIntegrator
import civictech.metagraph.MetaGraph
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.REACTIVE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class SpringConfig {

    private val log = LoggerFactory.getLogger(javaClass)

    @Bean
    fun emptyDeliberation(): Deliberation = Deliberation(
        metaGraph = MetaGraph(DeliberationIntegrator()),
        agentDefs = mutableMapOf()
    )

    @Bean
    @ConditionalOnWebApplication(type = REACTIVE)
    fun commandLineRunner(): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? -> log.info("Running as reactive web application.") }
    }
}