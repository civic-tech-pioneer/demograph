package civictech.metagraph

import java.util.*

data class EdgeDef<Data>(
    override val id: UUID = UUID.randomUUID(),
    val sourceRef: UUID,
    val targetRef: UUID,
    override val data: Data? = null
) : MemberDef<Data> {
    init {
        require(id != sourceRef) { "Cannot connect from self" }
        require(id != targetRef) { "Cannot connect to self" }
    }
}