package civictech.metagraph.view

import civictech.metagraph.MetaGraph
import civictech.metagraph.Quantifiable
import civictech.metagraph.def.NodeDef

data class Node<In, Out : Quantifiable>(
    override val metaGraph: MetaGraph<In, Out>,
    override val def: NodeDef<In, Out>
) : Member<In, Out>() {
    override fun toString(): String = "Node[id:${def.id}, data:${def.data}, integrated:${def.integrated}]"
}