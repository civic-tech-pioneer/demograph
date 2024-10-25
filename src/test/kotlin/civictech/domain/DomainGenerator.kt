package civictech.domain

import civictech.dto.AgentDef
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.uuid
import io.kotest.property.arbs.name

val agentArb: Arb<AgentDef> = arbitrary {
    AgentDef(
        Arb.uuid().bind(),
        Arb.name().map { "${it.first.name} ${it.last.name}" }.bind()
    )
}