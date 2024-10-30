package civictech.metagraph

import java.util.*

data class NodeDef<In>(
    override val id: UUID = UUID.randomUUID(),
    override var data: In? = null
) : MemberDef<In>
