package civictech.deliberate.view

import civictech.deliberate.def.AgentDef
import civictech.deliberate.def.ContestableDef
import civictech.deliberate.domain.Degree

class Belief(
    val contestable: Contestable,
    val agentDef: AgentDef
    ) {

    var strength: Degree?
        get() = contestableDef?.beliefs?.get(agentDef)
        set(value) {
            if (value == null) {
                contestableDef?.beliefs?.remove(agentDef)
            } else {
//                contestableDef?.beliefs?.get(agentDef) = value
            }
        }

    val contestableDef: ContestableDef?
        get() = contestable.member.data

//    val agent: Agent
//        get() = deliberation.agent(agentDef.id)!!
//
//    val degree: Degree?
//        get() = contestableDef.beliefs[agentDef]
}