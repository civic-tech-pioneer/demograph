package civictech.metagraph

import java.util.*

abstract class Member<In, Out: Credence> {
    abstract val metaGraphDef: MetaGraphDef<In, Out>
    abstract val def: MemberDef<In>
    private var _integrated: Out? = null

    val id: UUID
        get() = def.id

    var data: In?
        get() = def.data
        set(value) {
            def.data = value
            metaGraphDef.queuePropagation(this)
        }

    var integrated: Out?
        get() = _integrated
        internal set(value) {
            _integrated = value
        }

    val incoming: List<Edge<In, Out>>
        get() = metaGraphDef.incoming(id)

    val outgoing: List<Edge<In, Out>>
        get() = metaGraphDef.outgoing(id)
}
