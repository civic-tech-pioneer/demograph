package civictech.metagraph

import java.util.*

class MetaGraphDef<In, Out: Credence>(
    private val integrator: Integrator<In, Out>,
    private val members: MutableMap<UUID, MemberDef<In>> = mutableMapOf(),
    private val sourceIndex: MutableMap<UUID, MutableSet<EdgeDef<In>>> = mutableMapOf(),
    private val targetIndex: MutableMap<UUID, MutableSet<EdgeDef<In>>> = mutableMapOf()
) : Map<UUID, Member<In, Out>> {

    /* === Members Map API ===  */
    override val entries: Set<Map.Entry<UUID, Member<In, Out>>>
        get() = members.mapValues { fromDef(it.value) }.entries

    override val size: Int = members.size

    override fun isEmpty(): Boolean = members.isEmpty()

    override val keys: Set<UUID>
        get() = members.keys

    override val values: Collection<Member<In, Out>>
        get() = members.values.map { fromDef(it) }

    override fun containsKey(key: UUID): Boolean = members.containsKey(key)

    override fun containsValue(value: Member<In, Out>): Boolean = members.containsKey(value.id)

    override fun get(key: UUID): Member<In, Out>? = members[key]?.let { fromDef(it) }

    fun add(memberDef: MemberDef<In>) {
        members += memberDef.id to memberDef
        if(memberDef is EdgeDef<In>) {
            sourceIndex.getOrPut(memberDef.sourceRef) { mutableSetOf() }.add(memberDef)
            targetIndex.getOrPut(memberDef.targetRef) { mutableSetOf() }.add(memberDef)
        }
    }

    /* === Traversal === */

    fun incoming(id: UUID): List<Edge<In, Out>> =
        targetIndex[id]?.map { Edge(this, it) } ?: emptyList()

    fun outgoing(id: UUID): List<Edge<In, Out>> =
        sourceIndex[id]?.map { Edge(this, it) } ?: emptyList()


    private fun fromDef(memberDef: MemberDef<In>): Member<In, Out> = when (memberDef) {
        is NodeDef<In> -> Node(this, def = memberDef)
        is EdgeDef<In> -> Edge(this, def = memberDef)
        else -> throw IllegalArgumentException()
    }

    internal fun queuePropagation(member: Member<In, Out>) {
        val edgeDefs = sourceIndex[member.id] ?: mutableSetOf()
        TODO("Need to implement propagation. E.g. integrate the change, propagate it.")
//        edgeDefs.forEach{ edge -> edge.data. }
    }

    companion object {
        fun <In, Out: Credence> withMembers(integrator: Integrator<In, Out>, vararg members: MemberDef<In>): MetaGraphDef<In, Out> =
            withMembers(integrator, members.toList())

        fun <In, Out: Credence> withMembers(integrator: Integrator<In, Out>, members: Collection<MemberDef<In>>): MetaGraphDef<In, Out> {
            val edges = members.filterIsInstance<EdgeDef<In>>()
            return MetaGraphDef(
                integrator,
                members.associateBy { it.id }.toMutableMap(),
                edges.groupBy { it.sourceRef }.mapValues { it.value.toMutableSet() }.toMutableMap(),
                edges.groupBy { it.targetRef }.mapValues { it.value.toMutableSet() }.toMutableMap()
            )
        }
    }
}
