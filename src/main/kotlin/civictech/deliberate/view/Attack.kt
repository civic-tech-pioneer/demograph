package civictech.deliberate.view

import civictech.deliberate.Deliberation
import civictech.deliberate.def.ContestableDef
import civictech.deliberate.domain.Credence
import civictech.metagraph.view.Edge

class Attack(
    override val deliberation: Deliberation,
    override val member: Edge<ContestableDef, Credence>,
) : Relation() {


}