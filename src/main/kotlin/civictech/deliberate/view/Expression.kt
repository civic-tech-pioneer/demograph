package civictech.deliberate.view

import civictech.deliberate.Deliberation
import civictech.deliberate.def.ContestableDef
import civictech.deliberate.def.ExpressionDef
import civictech.deliberate.domain.Credence
import civictech.metagraph.view.Node

class Expression(
    override val deliberation: Deliberation,
    override val member: Node<ContestableDef, Credence>,
) : Contestable {

    override val def: ExpressionDef?
        get() = member.dataAs()
}