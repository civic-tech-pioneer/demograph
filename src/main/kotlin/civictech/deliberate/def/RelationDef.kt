package civictech.deliberate.def

import civictech.deliberate.domain.Degree
import java.util.*

data class RelationDef(
    override val id: UUID = UUID.randomUUID(),
    val semantics: Semantics,
    val from: UUID,
    val to: UUID,
    override val beliefs: MutableMap<AgentDef, Degree> = mutableMapOf()
) : ContestableDef {

        companion object {
            enum class Semantics {
                Support,
                Attack
            }
        }
}