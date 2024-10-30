package civictech.metagraph

interface Integrator<In, Out: Credence> {
    fun integrate(member: Member<In, Out>): Out = when (member) {
        is Node<In, Out> -> integrateNode(member)
        is Edge<In, Out> -> integrateEdge(member)
        else -> throw IllegalArgumentException("Only nodes and edges are supported")
    }

    fun integrateNode(node: Node<In, Out>) : Out
    fun integrateEdge(edge: Edge<In, Out>) : Out
}