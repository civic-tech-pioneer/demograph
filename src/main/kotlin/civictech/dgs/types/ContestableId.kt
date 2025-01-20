package civictech.dgs.types

import java.util.*

data class ContestableId(override val id: UUID): Contestable {
    override val sourceLinks: List<Link>
        get() = emptyList()
    override val targetLinks: List<Link>
        get() = emptyList()
    override val owner: Agent?
        get() = null
}