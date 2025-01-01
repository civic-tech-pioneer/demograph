package civictech.deliberate.def

import civictech.deliberate.domain.Addressable
import java.util.*

data class AgentDef(override val id: UUID, var displayName: String) : Addressable