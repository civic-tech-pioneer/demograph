package civictech.deliberate.view

import civictech.deliberate.def.ContestableDef
import civictech.deliberate.def.RelationDef
import civictech.deliberate.def.RelationDef.Companion.Semantics
import civictech.deliberate.domain.Credence
import civictech.metagraph.view.Edge
import civictech.metagraph.view.Node
import java.util.*

abstract class Relation : Contestable {
    abstract override val member: Edge<ContestableDef, Credence>

    override val id: UUID
        get() = member.id

    override val def: RelationDef?
        get() = member.dataAs()

    val source: Expression?
        get() = member.source?.let {
            when {
                it is Node -> Expression(deliberation, it)
                else -> null
            }
        }

    val target: Contestable?
        get() = member.target?.let { when(it) {
            is Node -> Expression(deliberation, it)
            is Edge -> {
                when(val data = it.def.data) {
                    is RelationDef ->
                        when(data.semantics) {
                            Semantics.Support -> Support(deliberation, it)
                            Semantics.Attack -> Attack(deliberation, it)
                        }
                    else -> null
                }
            }
            else -> null
        }
    }
}