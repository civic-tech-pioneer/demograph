package civictech.metagraph.def

import civictech.metagraph.Quantifiable
import java.util.*

data class NodeDef<In, Out : Quantifiable>(
    override val id: UUID = UUID.randomUUID(),
    override val initialData: In? = null
) : MemberDef<In, Out>() {
    init {
        data = initialData
    }
}
