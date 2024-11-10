package civictech.metagraph.view

import civictech.metagraph.MetaGraph
import civictech.metagraph.Quantifiable
import civictech.metagraph.def.MemberDef
import java.util.*
import kotlin.math.abs

abstract class Member<In, Out : Quantifiable> {

    abstract val metaGraph: MetaGraph<In, Out>
    abstract val def: MemberDef<In, Out>

    val id: UUID
        get() = def.id

    var data: In?
        get() = def.data
        set(value) {
            def.data = value
            integrated = metaGraph.integrator.integrate(this)
        }

    inline fun <reified T : In> dataAs(): T? {
        return if (data is T) data as T else null
    }

    var integrated: Out?
        get() = def.integrated
        internal set(value) {
            val previous: Out? = def.integrated
            def.integrated = value
            // priority is 1 if this is the first value, or the difference between old and new otherwise
            // TODO: consider prioritizing by number of edges with `integrated == null`, e.g. prioritize areas
            // where we may be able to eliminate some of these nulls, which prevents calculating certain members
            // over and over.
            val priority = if (previous == null || value == null) 1f else abs(previous.score - value.score)
            metaGraph.queuePropagation(this, priority)
        }

    val incoming: List<Edge<In, Out>>
        get() = metaGraph.incoming(id)

    val outgoing: List<Edge<In, Out>>
        get() = metaGraph.outgoing(id)
}
