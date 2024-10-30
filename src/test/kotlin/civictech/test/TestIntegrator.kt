package civictech.test

import civictech.metagraph.Quantifiable
import civictech.metagraph.view.Edge
import civictech.metagraph.Integrator
import civictech.metagraph.view.Node
import kotlin.math.round

object TestIntegrator : Integrator<Int, Median> {

    override fun integrateNode(node: Node<Int, Median>): Median {
        return Median.from(
            node.data,
            *node.incoming.map { it.integrated?.value }.toTypedArray()
        )
    }

    override fun integrateEdge(edge: Edge<Int, Median>): Median {
        return Median.from(
            edge.data,
            edge.source?.integrated?.value,
            *edge.incoming.map { it.integrated?.value }.toTypedArray()
        )
    }
}

data class Median(val ints: List<Int>) : Quantifiable {

    val value: Int? by lazy {
        val sortedInts = ints.sorted()
        val length = sortedInts.size
        if (length == 0) {
            null
        } else if (length % 2 == 0) {
            val sumOfMiddleValues = (sortedInts[length / 2] + sortedInts[(length / 2) - 1])
            round(sumOfMiddleValues / 2.0).toInt()
        } else {
            sortedInts[length / 2]
        }
    }

    override val score: Float
        get() = (value?.toFloat() ?: 0f) / Int.MAX_VALUE

    companion object {
        fun from(vararg ints: Int?): Median = Median(ints.filterNotNull())
    }
}
