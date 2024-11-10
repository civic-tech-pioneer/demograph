package civictech.config

import civictech.deliberate.Deliberation
import civictech.deliberate.DeliberationIntegrator
import civictech.metagraph.MetaGraph
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SpringConfig {

    @Bean
    fun emptyDeliberation(): Deliberation = Deliberation(
        metaGraph = MetaGraph(DeliberationIntegrator()),
        agentDefs = mutableMapOf()
    )
}