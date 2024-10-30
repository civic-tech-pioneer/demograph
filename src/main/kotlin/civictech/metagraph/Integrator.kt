package civictech.metagraph

interface Integrator<In, Out: Credence> {
    fun integrateNode(node: Node<In, Out>) : Out
    fun integrateEdge(edge: Edge<In, Out>) : Out
}