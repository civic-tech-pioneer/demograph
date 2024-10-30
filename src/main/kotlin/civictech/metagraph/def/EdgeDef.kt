package civictech.metagraph.def

import civictech.metagraph.Quantifiable
import java.util.*

data class EdgeDef<In, Out : Quantifiable>(
    override val id: UUID = UUID.randomUUID(),
    val sourceRef: UUID,
    val targetRef: UUID,
    override val initialData: In? = null
) : MemberDef<In, Out>() {
    init {
        require(id != sourceRef) { "Cannot connect from self" }
        require(id != targetRef) { "Cannot connect to self" }
        data = initialData
    }
}