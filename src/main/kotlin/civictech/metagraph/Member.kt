package civictech.metagraph

import java.util.*

abstract class Member<Data> {
    abstract val metaGraphDef: MetaGraphDef<Data>
    abstract val def: MemberDef<Data>

    val id: UUID
        get() = def.id

    val data: Data?
        get() = def.data

    val incoming: List<Edge<Data>>
        get() = metaGraphDef.incoming(id)

    val outgoing: List<Edge<Data>>
        get() = metaGraphDef.outgoing(id)
}
