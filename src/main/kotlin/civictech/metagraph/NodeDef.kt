package civictech.metagraph

import java.util.*

data class NodeDef<Data>(
    override val id: UUID = UUID.randomUUID(),
    override var data: Data? = null
) : MemberDef<Data>
