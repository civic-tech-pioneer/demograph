package civictech.metagraph

import java.util.UUID

data class NodeDef<Data>(val id: UUID = UUID.randomUUID(), val data: Data? = null) {

}
