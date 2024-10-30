package civictech.metagraph

data class Node<In, Out: Credence>(
    override val metaGraphDef: MetaGraphDef<In, Out>,
    override val def: NodeDef<In>
) : Member<In, Out>()