package civictech.metagraph

data class Traversal<Data>(
    val metaGraphDef: MetaGraphDef<Data>) {

    fun run(visitor: (Member<Data>) -> Member<Data>) {

    }
}
