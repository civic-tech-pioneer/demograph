package civictech.metagraph

import civictech.metagraph.def.EdgeDef
import civictech.metagraph.def.MemberDef
import civictech.metagraph.def.NodeDef
import civictech.metagraph.view.Edge
import civictech.metagraph.view.Member
import civictech.metagraph.view.Node
import java.util.*

class MetaGraph<In, Out : Quantifiable>(
    internal val integrator: Integrator<In, Out>,
    private val members: MutableMap<UUID, MemberDef<In, Out>> = mutableMapOf(),
    private val sourceIndex: MutableMap<UUID, MutableSet<EdgeDef<In, Out>>> = mutableMapOf(),
    private val targetIndex: MutableMap<UUID, MutableSet<EdgeDef<In, Out>>> = mutableMapOf()
) : Map<UUID, Member<In, Out>> {

    private val queue: PriorityQueue<Update<In, Out>> = PriorityQueue()

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

    fun add(memberDef: MemberDef<In, Out>) {
        members += memberDef.id to memberDef
        if (memberDef is EdgeDef<In, Out>) {
            sourceIndex.getOrPut(memberDef.sourceRef) { mutableSetOf() }.add(memberDef)
            targetIndex.getOrPut(memberDef.targetRef) { mutableSetOf() }.add(memberDef)
        }
    }

    /* === Traversal === */

    fun incoming(id: UUID): List<Edge<In, Out>> =
        targetIndex[id]?.map { Edge(this, it) } ?: emptyList()

    fun outgoing(id: UUID): List<Edge<In, Out>> =
        sourceIndex[id]?.map { Edge(this, it) } ?: emptyList()

    /* === Propagation | Fixpoint calculation === */
    val isFixPoint
        get(): Boolean = queue.isEmpty()

    fun propagateUpdate() {
        val member = queue.remove().member
        member.integrated = integrator.integrate(member)
    }

    internal fun queuePropagation(member: Member<In, Out>, priority: Float) {
        outgoing(member.id).forEach {
            queue.add(Update(it, priority))
        }
        if (member is Edge<In, Out>) {
            member.target?.let { queue.add(Update(it, priority)) }
        }
    }

    private fun fromDef(memberDef: MemberDef<In, Out>): Member<In, Out> = when (memberDef) {
        is NodeDef<In, Out> -> Node(this, def = memberDef)
        is EdgeDef<In, Out> -> Edge(this, def = memberDef)
        else -> throw IllegalArgumentException()
    }

    override fun toString(): String {
        return values.toString()
    }

    companion object {
        internal data class Update<In, Out : Quantifiable>(val member: Member<In, Out>, val priority: Float) :
            Comparable<Update<In, Out>> {
            override fun compareTo(other: Update<In, Out>): Int =
                this.priority.compareTo(other.priority)
        }

        fun <In, Out : Quantifiable> withMembers(
            integrator: Integrator<In, Out>,
            vararg members: MemberDef<In, Out>
        ): MetaGraph<In, Out> =
            withMembers(integrator, members.toList())

        fun <In, Out : Quantifiable> withMembers(
            integrator: Integrator<In, Out>,
            members: Collection<MemberDef<In, Out>>
        ): MetaGraph<In, Out> {
            val edges = members.filterIsInstance<EdgeDef<In, Out>>()
            return MetaGraph(
                integrator,
                members.associateBy { it.id }.toMutableMap(),
                edges.groupBy { it.sourceRef }.mapValues { it.value.toMutableSet() }.toMutableMap(),
                edges.groupBy { it.targetRef }.mapValues { it.value.toMutableSet() }.toMutableMap()
            )
        }
    }
}
