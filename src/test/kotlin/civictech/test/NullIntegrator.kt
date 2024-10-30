package civictech.test

import civictech.metagraph.Credence
import civictech.metagraph.Edge
import civictech.metagraph.Integrator
import civictech.metagraph.Node

object NullIntegrator : Integrator<Null, Null> {
    override fun integrateNode(node: Node<Null, Null>): Null = Null

    override fun integrateEdge(edge: Edge<Null, Null>): Null = Null
}
object Null : Credence {
    override val score: Float
        get() = 0.5f
}