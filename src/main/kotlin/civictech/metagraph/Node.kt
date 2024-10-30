package civictech.metagraph

data class Node<In, Out: Credence>(
    override val metaGraphDef: MetaGraphDef<In, Out>,
    override val def: NodeDef<In, Out>
) : Member<In, Out>() {
    override fun toString(): String = "Node[id:${def.id}, data:${def.data}, integrated:${def.integrated}]"
}