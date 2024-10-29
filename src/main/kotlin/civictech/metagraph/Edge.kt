package civictech.metagraph

import java.util.*

data class Edge<Data>(
    override val metaGraphDef: MetaGraphDef<Data>,
    override val def: EdgeDef<Data>
) : Member<Data>() {

    val sourceRef: UUID
        get() = def.sourceRef
    val targetRef: UUID
        get() = def.targetRef

    val source: Member<Data>?
        get() = metaGraphDef[sourceRef]

    val target: Member<Data>?
        get() = metaGraphDef[targetRef]


}
