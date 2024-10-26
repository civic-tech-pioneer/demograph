package civictech.metagraph

import java.util.*

data class EdgeDef<Data>(val id: UUID = UUID.randomUUID(), val fromRef: UUID, val toRef: UUID, val data: Data? = null) {
    init {
        require(id != fromRef) { "Cannot connect from self" }
        require(id != toRef) { "Cannot connect to self" }
    }
}