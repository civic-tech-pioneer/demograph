package civictech.controller

import civictech.deliberate.service.MetArGraphService
import civictech.dgs.types.Agent
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux

@Controller
class QueryController(private val agentService: MetArGraphService) {
//    @SubscriptionMapping
    fun agentUpdates(): Flux<Agent> =
        agentService.getAgentStream().map { Agent({ it.id }, { it.displayName }) }
}