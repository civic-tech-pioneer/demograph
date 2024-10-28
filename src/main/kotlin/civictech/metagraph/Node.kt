package civictech.metagraph

data class Node<Data>(
    override val metaGraph: MetaGraph<Data>,
    override val def: NodeDef<Data>
) : Member<Data>()