package civictech.metagraph

data class Node<Data>(
    override val metaGraphDef: MetaGraphDef<Data>,
    override val def: NodeDef<Data>
) : Member<Data>()