package civictech.deliberate.graphql.datafetchers

import civictech.deliberate.service.MetArGraphService
import civictech.dgs.types.Agent
import com.netflix.graphql.dgs.*
import org.reactivestreams.Publisher
import java.util.*

@DgsComponent
class AgentDataFetcher(
    private val agentService: MetArGraphService
) {

    /**
     * This dataFetcher resolves the agents field on Query.
     * It uses an @InputArgument to get the id from the Query if one is defined.
     */
    @DgsQuery
    fun agents(@InputArgument("id") id: UUID?): List<Agent> {
        return agentService.agents(id).map {
            Agent({ it.displayName })
        }
    }

    @DgsMutation
    fun addAgent(@InputArgument("displayName") displayName: String): Agent {
        val added = agentService.addAgent(displayName)
        return Agent({ added.displayName })
    }

    @DgsMutation
    fun updateAgent(@InputArgument("id") id: UUID, @InputArgument("displayName") displayName: String): Agent? {
        val updated = agentService.updateAgent(id, displayName)
        return updated?.let { Agent({ it.displayName }) }
    }

    @DgsSubscription
    fun agentUpdates(): Publisher<Agent> =
        agentService.getAgentStream().map { Agent({ it.displayName }) }
}