package civictech.deliberate.def

import civictech.deliberate.domain.Degree
import java.util.*

data class ExpressionDef(
    var text: String,
    override val id: UUID = UUID.randomUUID(),
    override val beliefs: MutableMap<AgentDef, Degree> = mutableMapOf()
) : ContestableDef {

}
