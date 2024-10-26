package civictech.metagraph

import java.util.*

data class Node<Data>(override val metaGraph: MetaGraph<Data>,
                      val def: NodeDef<Data>
) : Member<Data>() {
    override val id: UUID
        get() = def.id
    override val data: Data?
        get() = def.data
}