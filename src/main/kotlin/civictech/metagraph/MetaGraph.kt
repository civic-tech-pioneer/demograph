package civictech.metagraph

import java.util.*

class MetaGraph<Data>(
    val members: Map<UUID, MemberDef<Data>> = mapOf(),
    val fromIndex: Map<UUID, List<EdgeDef<Data>>> = mapOf(),
    val toIndex: Map<UUID, List<EdgeDef<Data>>> = mapOf()
) : Map<UUID, Member<Data>> {

    /* === Members Map API ===  */
    override val entries: Set<Map.Entry<UUID, Member<Data>>>
        get() = members.mapValues { fromDef(it.value) }.entries

    override val size: Int = members.size

    override fun isEmpty(): Boolean = members.isEmpty()

    override val keys: Set<UUID>
        get() = members.keys

    override val values: Collection<Member<Data>>
        get() = members.values.map { fromDef(it) }

    override fun containsKey(key: UUID): Boolean = members.containsKey(key)

    override fun containsValue(value: Member<Data>): Boolean = members.containsKey(value.id)

    override fun get(key: UUID): Member<Data>? = members[key]?.let { fromDef(it) }

    /* === Traversal === */

    fun incoming(id: UUID): List<Edge<Data>> =
        toIndex[id]?.map { Edge(this, it) } ?: emptyList()

    fun outgoing(id: UUID): List<Edge<Data>> =
        fromIndex[id]?.map { Edge(this, it) } ?: emptyList()

    fun traversal(): Traversal<Data> = Traversal(this)



    private fun fromDef(memberDef: MemberDef<Data>): Member<Data> = when (memberDef) {
        is NodeDef<Data> -> Node(this, def = memberDef)
        is EdgeDef<Data> -> Edge(this, def = memberDef)
        else -> throw IllegalArgumentException()
    }

    companion object {
        fun <Data> withMembers(vararg members: MemberDef<Data>): MetaGraph<Data> =
            withMembers(members.toList())

        fun <Data> withMembers(members: Collection<MemberDef<Data>>): MetaGraph<Data> {
            val edges = members.filterIsInstance<EdgeDef<Data>>()
            return MetaGraph(
                members.associateBy { it.id },
                edges.groupBy { it.sourceRef },
                edges.groupBy { it.targetRef }
            )
        }
    }
}
