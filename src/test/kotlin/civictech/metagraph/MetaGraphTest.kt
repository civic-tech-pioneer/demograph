package civictech.metagraph

import civictech.metagraph.def.EdgeDef
import civictech.metagraph.def.NodeDef
import civictech.metagraph.view.Edge
import civictech.metagraph.view.Node
import civictech.test.Median
import civictech.test.Null
import civictech.test.NullIntegrator
import civictech.test.TestIntegrator
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.contain
import io.kotest.matchers.maps.shouldContainValue
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import java.util.*

class MetaGraphTest : StringSpec({
    "MetaGraph should be initialized empty" {
        MetaGraph(NullIntegrator) should beEmpty()
    }

    "MetaGraph should allow providing nodes" {
        val nodeDef1 = NodeDef<Null, Null>()
        val nodeDef2 = NodeDef<Null, Null>()
        val metaGraph = MetaGraph.withMembers(NullIntegrator, nodeDef1, nodeDef2)

        metaGraph should contain(nodeDef1.id, Node(metaGraph, nodeDef1))
        metaGraph should contain(nodeDef2.id, Node(metaGraph, nodeDef2))
    }

    "MetaGraph should allow providing edges" {
        val edgeDef1 = EdgeDef<Null, Null>(
            sourceRef = UUID.randomUUID(),
            targetRef = UUID.randomUUID(),
        )
        val edgeDef2 = EdgeDef<Null, Null>(
            sourceRef = UUID.randomUUID(),
            targetRef = UUID.randomUUID(),
        )
        val metaGraph = MetaGraph.withMembers(NullIntegrator, edgeDef1, edgeDef2)

        metaGraph should contain(edgeDef1.id, Edge(metaGraph, edgeDef1))
        metaGraph should contain(edgeDef2.id, Edge(metaGraph, edgeDef2))
    }

    "MetaGraph should allow basic navigation" {
        val nodeDef1 = NodeDef<Null, Null>()
        val nodeDef2 = NodeDef<Null, Null>()
        val edgeDef1 = EdgeDef<Null, Null>(
            sourceRef = nodeDef1.id,
            targetRef = nodeDef2.id,
        )
        val edgeDef2 = EdgeDef<Null, Null>(
            sourceRef = edgeDef1.id,
            targetRef = edgeDef1.id,
        )

        val metaGraph = MetaGraph.withMembers(
            NullIntegrator,
            nodeDef1,
            nodeDef2,
            edgeDef1,
            edgeDef2,
        )

        val node1 = metaGraph[nodeDef1.id]
        val node2 = metaGraph[nodeDef2.id]
        val edge1 = metaGraph[edgeDef1.id]
        val edge2 = metaGraph[edgeDef2.id]

        node1 shouldNot beNull()
        node2 shouldNot beNull()
        edge1 shouldNot beNull()
        edge2 shouldNot beNull()

        node1!!.incoming shouldBe emptyList()
        node1.outgoing should containOnly(Edge(metaGraph, edgeDef1))

        node2!!.incoming should containOnly(Edge(metaGraph, edgeDef1))
        node2.outgoing shouldBe emptyList()

        edge1!!.incoming should containOnly(Edge(metaGraph, edgeDef2))
        edge1.outgoing should containOnly(Edge(metaGraph, edgeDef2))
        (edge1 as Edge<Null, Null>).source shouldBe node1
        (edge1).target shouldBe node2

        edge2!!.incoming shouldBe emptyList()
        edge2.outgoing shouldBe emptyList()
        (edge2 as Edge<Null, Null>).source shouldBe edge1
        edge2.target shouldBe edge1
    }

    "MetaGraph should allow adding members at runtime" {
        val nodeDef1 = NodeDef<Null, Null>()
        val nodeDef2 = NodeDef<Null, Null>()
        val edgeDef1 = EdgeDef<Null, Null>(
            sourceRef = nodeDef1.id,
            targetRef = nodeDef2.id,
        )

        val metaGraph = MetaGraph(NullIntegrator)

        metaGraph.apply {
            add(nodeDef1)
            add(nodeDef2)
            add(edgeDef1)
        }

        metaGraph shouldContainValue Node(metaGraph, nodeDef1)
        metaGraph shouldContainValue Node(metaGraph, nodeDef2)
        metaGraph shouldContainValue Edge(metaGraph, edgeDef1)

        // and navigatino should be possible
        val node1 = metaGraph[nodeDef1.id]!!
        val node2 = metaGraph[nodeDef2.id]!!
        val edge1 = metaGraph[edgeDef1.id]!! as Edge<Null, Null>

        node1.outgoing should containOnly(edge1)
        node2.incoming should containOnly(edge1)
        edge1.source shouldBe node1
        edge1.target shouldBe node2
    }

    "MetaGraph should allow updating data" {
        val nodeDef1 = NodeDef<Int, Median>()
        val metaGraph = MetaGraph.withMembers(TestIntegrator, nodeDef1)
        metaGraph[nodeDef1.id]?.data = 10
        metaGraph[nodeDef1.id]?.data shouldBe 10
    }

    "MetaGraph should propagate updated information" {
        // given
        val nodeDef1 = NodeDef<Int, Median>()
        val nodeDef2 = NodeDef<Int, Median>()
        val edgeDef1 = EdgeDef<Int, Median>(
            sourceRef = nodeDef1.id,
            targetRef = nodeDef2.id,
        )
        val metaGraph = MetaGraph.withMembers(TestIntegrator, nodeDef1, nodeDef2, edgeDef1)

        // when
        metaGraph[nodeDef1.id]?.data = 10

        // then we are no longer in a fixpoint
        metaGraph.isFixPoint shouldBe false
        // and when we propagate the update to the edge
        metaGraph.propagateUpdate()
        // then we're still not in a fixpoint
        metaGraph.isFixPoint shouldBe false
        // and when we propagate the update to the node
        metaGraph.propagateUpdate()
        // then we are in a fixpoint
        metaGraph.isFixPoint shouldBe true

        // and the integrated value should now match the original one (median of one value)
        metaGraph[nodeDef2.id]?.integrated?.value shouldBe 10
        print(metaGraph)
    }
})