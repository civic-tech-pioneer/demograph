package civictech.metagraph

data class Traversal<Data>(
    val metaGraph: MetaGraph<Data>) {

    fun run(visitor: (Member<Data>) -> Member<Data>) {

    }
}
