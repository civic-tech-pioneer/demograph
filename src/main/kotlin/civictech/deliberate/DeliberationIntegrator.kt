package civictech.deliberate

import civictech.deliberate.def.ContestableDef
import civictech.deliberate.domain.Credence
import civictech.metagraph.Integrator
import civictech.metagraph.view.Edge
import civictech.metagraph.view.Node

class DeliberationIntegrator : Integrator<ContestableDef, Credence> {
    override fun integrateNode(node: Node<ContestableDef, Credence>): Credence {
        TODO("Not yet implemented")
    }

    override fun integrateEdge(edge: Edge<ContestableDef, Credence>): Credence {
        TODO("Not yet implemented")
    }
}