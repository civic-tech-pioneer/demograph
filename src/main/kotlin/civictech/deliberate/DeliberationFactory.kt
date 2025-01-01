package civictech.deliberate

import civictech.deliberate.def.AgentDef
import civictech.deliberate.def.ContestableDef
import civictech.deliberate.def.ExpressionDef
import civictech.deliberate.def.RelationDef
import civictech.deliberate.domain.Credence
import civictech.metagraph.MetaGraph
import civictech.metagraph.def.EdgeDef
import civictech.metagraph.def.NodeDef

object DeliberationFactory {
    fun from(
        agentDefs: Collection<AgentDef> = listOf(),
        expressions: Collection<ExpressionDef> = listOf(),
        relations: Collection<RelationDef> = listOf(),
    ): Deliberation {

        val nodes = expressions.map {
            NodeDef<ContestableDef, Credence>(
                id = it.id,
                initialData = it
            )
        }

        val edges = relations.map {
            EdgeDef<ContestableDef, Credence>(
                id = it.id,
                sourceRef = it.from,
                targetRef = it.to,
                initialData = it
            )
        }
        val members = nodes.plus(edges)
        val metaGraph = MetaGraph.withMembers(
            integrator = DeliberationIntegrator(),
            members = members
        )

        return Deliberation(
            metaGraph,
            agentDefs.associateBy { it.id }.toMutableMap()
        )
    }
}