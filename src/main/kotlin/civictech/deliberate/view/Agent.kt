package civictech.deliberate.view

import civictech.deliberate.Deliberation
import civictech.deliberate.def.AgentDef
import java.util.UUID

class Agent(
    val deliberation: Deliberation,
    val agentDef: AgentDef) {

    val id: UUID
        get() = agentDef.id

    val displayName: String
        get() = agentDef.displayName

    val beliefs: Set<Belief>
        get() = TODO()
}