package civictech.metagraph

import java.util.*
import kotlin.math.abs

abstract class Member<In, Out: Credence> {
    abstract val metaGraphDef: MetaGraphDef<In, Out>
    abstract val def: MemberDef<In, Out>

    val id: UUID
        get() = def.id

    var data: In?
        get() = def.data
        set(value) {
            def.data = value
            integrated = metaGraphDef.integrator.integrate(this)
        }

    var integrated: Out?
        get() = def.integrated
        internal set(value) {
            val previous: Out? = def.integrated
            def.integrated = value
            // priority is 1 if this is the first value, or the difference between old and new otherwise
            val priority = if (previous == null || value == null) 1f else abs(previous.score - value.score)
            metaGraphDef.queuePropagation(this, priority)
        }

    val incoming: List<Edge<In, Out>>
        get() = metaGraphDef.incoming(id)

    val outgoing: List<Edge<In, Out>>
        get() = metaGraphDef.outgoing(id)
}
