package civictech.metagraph

import java.util.UUID

class MetaGraph<Data>(
    val nodes: Map<UUID, NodeDef<Data>> = mapOf(),
    val edges: Map<UUID, EdgeDef<Data>> = mapOf(),
) : Map<UUID, Member<Data>>{

    override val size: Int = 0

    override fun isEmpty(): Boolean {
        return nodes.isEmpty() && edges.isEmpty()
    }

    /* === Member ===  */
    override val entries: Set<Map.Entry<UUID, Member<Data>>>
        get() = nodeEntries.union(edgeEntries)

    override val keys: Set<UUID>
        get() = nodeKeys.union(edgeKeys)

    override val values: Collection<Member<Data>>
        get() = nodeValues.union(edgeValues)

    override fun containsKey(key: UUID): Boolean =
        containsNodeKey(key) || containsEdgeKey(key)

    override fun containsValue(value: Member<Data>): Boolean {
        when(value) {
            is Node<Data> -> containsNodeValue(value)
            is Edge<Data> -> containsEdgeValue(value)
        }
        return false
    }

    override fun get(key: UUID): Member<Data>? = getNode(key) ?: getEdge(key)

    /* === Node === */
    private fun node(def: NodeDef<Data>): Node<Data> =
        Node(this, def)

    val nodeEntries: Set<Map.Entry<UUID, Node<Data>>>
        get() = nodes.mapValues{node(it.value)}.entries

    val nodeKeys: Set<UUID>
        get() = nodes.keys

    val nodeValues: Collection<Node<Data>>
        get() = nodes.values.map(::node)

    fun containsNodeKey(key: UUID): Boolean = nodes.containsKey(key)

    fun containsNodeValue(value: Node<Data>): Boolean = containsNodeKey(value.id)

    fun getNode(key: UUID): Node<Data>? = nodes[key]?.let(::node)

    /* === Edge === */
    private fun edge(def: EdgeDef<Data>): Edge<Data> =
        Edge(this, def)

    val edgeEntries: Set<Map.Entry<UUID, Edge<Data>>>
        get() = edges.mapValues{edge(it.value)}.entries

    val edgeKeys: Set<UUID>
        get() = edges.keys

    val edgeValues: Collection<Edge<Data>>
        get() = edges.values.map(::edge)

    fun containsEdgeKey(key: UUID): Boolean = edges.containsKey(key)

    fun containsEdgeValue(value: Edge<Data>): Boolean = containsEdgeKey(value.id)

    fun getEdge(key: UUID): Edge<Data>? = edges[key]?.let(::edge)

}
