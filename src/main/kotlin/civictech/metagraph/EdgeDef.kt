package civictech.metagraph

import java.util.*

data class EdgeDef<In>(
    override val id: UUID = UUID.randomUUID(),
    val sourceRef: UUID,
    val targetRef: UUID,
    override var data: In? = null
) : MemberDef<In> {
    init {
        require(id != sourceRef) { "Cannot connect from self" }
        require(id != targetRef) { "Cannot connect to self" }
    }
}