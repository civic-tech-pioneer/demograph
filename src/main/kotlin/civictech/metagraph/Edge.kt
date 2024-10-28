package civictech.metagraph

import java.util.*

data class Edge<Data>(
    override val metaGraph: MetaGraph<Data>,
    override val def: EdgeDef<Data>
) : Member<Data>() {

    val sourceRef: UUID
        get() = def.sourceRef
    val targetRef: UUID
        get() = def.targetRef

    val source: Member<Data>?
        get() = metaGraph[sourceRef]

    val target: Member<Data>?
        get() = metaGraph[targetRef]


}
