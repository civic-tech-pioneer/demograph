package civictech.metagraph

import java.util.*

data class NodeDef<In, Out: Credence>(
    override val id: UUID = UUID.randomUUID(),
    override val initialData: In? = null
) : MemberDef<In, Out>() {
    init {
        data = initialData
    }
}
