package civictech.deliberate.view

import civictech.deliberate.Deliberation
import civictech.deliberate.def.ContestableDef
import civictech.deliberate.domain.Addressable
import civictech.deliberate.domain.Credence
import civictech.metagraph.view.Member
import java.util.*

interface Contestable : Addressable {
    val deliberation: Deliberation
    val member: Member<ContestableDef, Credence>

    override val id: UUID
        get() = member.id

    val def: ContestableDef?

    val beliefs: Set<Belief>
        get() = member.data
            ?.beliefs
            ?.map { Belief(this, it.key) }
            ?.toSet()
            ?: setOf()
}