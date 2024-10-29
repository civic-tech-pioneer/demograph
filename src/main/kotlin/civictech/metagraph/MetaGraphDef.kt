package civictech.metagraph

import java.util.*

class MetaGraphDef<Data>(
    val members: MutableMap<UUID, MemberDef<Data>> = mutableMapOf(),
    val sourceIndex: MutableMap<UUID, MutableSet<EdgeDef<Data>>> = mutableMapOf(),
    val targetIndex: MutableMap<UUID, MutableSet<EdgeDef<Data>>> = mutableMapOf()
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

    fun add(memberDef: MemberDef<Data>) {
        members += memberDef.id to memberDef
        if(memberDef is EdgeDef<Data>) {
            sourceIndex.getOrPut(memberDef.sourceRef) { mutableSetOf() }.add(memberDef)
            targetIndex.getOrPut(memberDef.targetRef) { mutableSetOf() }.add(memberDef)
        }
    }

    /* === Traversal === */

    fun incoming(id: UUID): List<Edge<Data>> =
        targetIndex[id]?.map { Edge(this, it) } ?: emptyList()

    fun outgoing(id: UUID): List<Edge<Data>> =
        sourceIndex[id]?.map { Edge(this, it) } ?: emptyList()

    fun traversal(): Traversal<Data> = Traversal(this)



    private fun fromDef(memberDef: MemberDef<Data>): Member<Data> = when (memberDef) {
        is NodeDef<Data> -> Node(this, def = memberDef)
        is EdgeDef<Data> -> Edge(this, def = memberDef)
        else -> throw IllegalArgumentException()
    }

    companion object {
        fun <Data> withMembers(vararg members: MemberDef<Data>): MetaGraphDef<Data> =
            withMembers(members.toList())

        fun <Data> withMembers(members: Collection<MemberDef<Data>>): MetaGraphDef<Data> {
            val edges = members.filterIsInstance<EdgeDef<Data>>()
            return MetaGraphDef(
                members.associateBy { it.id }.toMutableMap(),
                edges.groupBy { it.sourceRef }.mapValues { it.value.toMutableSet() }.toMutableMap(),
                edges.groupBy { it.targetRef }.mapValues { it.value.toMutableSet() }.toMutableMap()
            )
        }
    }
}
