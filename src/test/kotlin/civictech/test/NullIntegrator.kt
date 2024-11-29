package civictech.test

import civictech.metagraph.Integrator
import civictech.metagraph.Quantifiable
import civictech.metagraph.view.Edge
import civictech.metagraph.view.Node

object NullIntegrator : Integrator<Null, Null> {
    override fun integrateNode(node: Node<Null, Null>): Null = Null

    override fun integrateEdge(edge: Edge<Null, Null>): Null = Null
}

object Null : Quantifiable {
    override val score: Float
        get() = 0.5f
}