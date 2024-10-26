package civictech.metagraph

import java.util.*

data class Edge<Data>(
    override val metaGraph: MetaGraph<Data>,
    val def: EdgeDef<Data>
) : Member<Data>() {
    override val id: UUID
        get() = def.id
    override val data: Data?
        get() = def.data
    val from: UUID
        get() = def.fromRef
    val to: UUID
        get() = def.toRef
}