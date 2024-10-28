package civictech.metagraph

import java.util.*

abstract class Member<Data> {
    abstract val metaGraph: MetaGraph<Data>
    abstract val def: MemberDef<Data>

    val id: UUID
        get() = def.id

    val data: Data?
        get() = def.data

    val incoming: List<Edge<Data>>
        get() = metaGraph.incoming(id)

    val outgoing: List<Edge<Data>>
        get() = metaGraph.outgoing(id)
}
