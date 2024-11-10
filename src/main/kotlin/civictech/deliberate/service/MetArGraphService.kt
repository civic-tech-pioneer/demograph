package civictech.deliberate.service

import civictech.deliberate.Deliberation
import civictech.deliberate.view.Agent
import org.springframework.stereotype.Service
import reactor.core.publisher.ConnectableFlux
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.*

@Service
class MetArGraphService(private var deliberation: Deliberation) {

    private lateinit var agentStream: FluxSink<Agent>
    private lateinit var agentsFlux: ConnectableFlux<Agent>

    init {
        val publisher = Flux.create { emitter: FluxSink<Agent> ->
            agentStream = emitter
        }
        agentsFlux = publisher.publish()
        agentsFlux.connect()
    }

    fun agents(id: UUID?): List<Agent> =
        id?.let { listOfNotNull(deliberation.agent(id)) } ?: deliberation.agents()

    fun addAgent(displayName: String): Agent {
        val added = deliberation.addAgent(displayName)
        agentStream.next(added)
        return added
    }

    fun updateAgent(id: UUID, displayName: String): Agent? =
        deliberation.updateAgent(id, displayName)?.also(agentStream::next)

    fun getAgentStream(): Flux<Agent> = agentsFlux
}