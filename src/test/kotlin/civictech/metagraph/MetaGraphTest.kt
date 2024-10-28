package civictech.metagraph

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.containOnly
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.contain
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import java.util.*

class MetaGraphTest : StringSpec({
    "MetaGraph should be initialized empty" {
        MetaGraph<String>() should beEmpty()
    }

    "MetaGraph should allow providing nodes" {
        val nodeDef1 = NodeDef<String>()
        val nodeDef2 = NodeDef<String>()
        val metaGraph = MetaGraph.withMembers(nodeDef1, nodeDef2)

        metaGraph should contain(nodeDef1.id, Node(metaGraph, nodeDef1))
        metaGraph should contain(nodeDef2.id, Node(metaGraph, nodeDef2))
    }

    "MetaGraph should allow providing edges" {
        val edgeDef1 = EdgeDef<Unit>(
            sourceRef = UUID.randomUUID(),
            targetRef = UUID.randomUUID(),
        )
        val edgeDef2 = EdgeDef<Unit>(
            sourceRef = UUID.randomUUID(),
            targetRef = UUID.randomUUID(),
        )
        val metaGraph = MetaGraph.withMembers(edgeDef1, edgeDef2)

        metaGraph should contain(edgeDef1.id, Edge(metaGraph, edgeDef1))
        metaGraph should contain(edgeDef2.id, Edge(metaGraph, edgeDef2))
    }

    "MetaGraph should allow basic navigation" {
        val nodeDef1 = NodeDef<String>()
        val nodeDef2 = NodeDef<String>()
        val edgeDef1 = EdgeDef<String>(
            sourceRef = nodeDef1.id,
            targetRef = nodeDef2.id,
        )
        val edgeDef2 = EdgeDef<String>(
            sourceRef = edgeDef1.id,
            targetRef = edgeDef1.id,
        )

        val metaGraph = MetaGraph.withMembers(
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
        (edge1 as Edge<String>).source shouldBe node1
        (edge1).target shouldBe node2

        edge2!!.incoming shouldBe emptyList()
        edge2.outgoing shouldBe emptyList()
        (edge2 as Edge<String>).source shouldBe edge1
        edge2.target shouldBe edge1
    }
})