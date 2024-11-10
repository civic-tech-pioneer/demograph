package civictech.deliberate

import civictech.deliberate.def.AgentDef
import civictech.deliberate.def.ContestableDef
import civictech.deliberate.domain.Credence
import civictech.deliberate.view.Agent
import civictech.metagraph.MetaGraph
import java.util.*

class Deliberation(
    val metaGraph: MetaGraph<ContestableDef, Credence>,
    val agentDefs: MutableMap<UUID, AgentDef>
) {

//    val agentBeliefsIndex: Map<UUID, Set<Belief>>

    fun agent(id: UUID): Agent? = agentDefs[id]?.let { Agent(this, it) }

    fun agents(): List<Agent> = agentDefs.values.map { Agent(this, it) }

    fun addAgent(displayName: String): Agent {
        val id = UUID.randomUUID()
        val agentDef = AgentDef(id, displayName)
        agentDefs += id to agentDef
        return Agent(this, agentDef)
    }

    fun updateAgent(id: UUID, displayName: String): Agent? {
        val agentDef = agentDefs[id]
        return agentDef?.let {
            it.displayName = displayName
            Agent(this, it)
        }
    }

//    fun contestable(id: UUID): Contestable? {
//        val member = metaGraph[id]
//        return when (val data = member?.data) {
//            is ExpressionDef -> Expression(this, member)
//            is RelationDef -> when(data.semantics) {
//                Semantics.Attack -> Attack(this, id, data)
//                Semantics.Support -> Support(this, id, data)
//            }
//            else -> null
//        }
//    }

}
