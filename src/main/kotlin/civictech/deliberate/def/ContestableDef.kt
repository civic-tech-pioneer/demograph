package civictech.deliberate.def

import civictech.deliberate.domain.Addressable
import civictech.deliberate.domain.Degree

interface ContestableDef : Addressable {
    val beliefs: MutableMap<AgentDef, Degree>
}