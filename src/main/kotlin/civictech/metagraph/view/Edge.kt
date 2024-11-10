package civictech.metagraph.view

import civictech.metagraph.MetaGraph
import civictech.metagraph.Quantifiable
import civictech.metagraph.def.EdgeDef
import java.util.*

data class Edge<In, Out : Quantifiable>(
    override val metaGraph: MetaGraph<In, Out>,
    override val def: EdgeDef<In, Out>
) : Member<In, Out>() {

    val sourceRef: UUID
        get() = def.sourceRef

    val targetRef: UUID
        get() = def.targetRef

    val source: Member<In, Out>?
        get() = metaGraph[sourceRef]

    val target: Member<In, Out>?
        get() = metaGraph[targetRef]

    override fun toString(): String =
        """Edge[id:${def.id}, source:$sourceRef, target:$targetRef, data:${def.data}, integrated:${def.integrated}]"""
}
