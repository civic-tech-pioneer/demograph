package civictech.metagraph

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
        MetaGraphDef<String>() should beEmpty()
    }

    "MetaGraph should allow providing nodes" {
        val nodeDef1 = NodeDef<String>()
        val nodeDef2 = NodeDef<String>()
        val metaGraphDef = MetaGraphDef.withMembers(nodeDef1, nodeDef2)

        metaGraphDef should contain(nodeDef1.id, Node(metaGraphDef, nodeDef1))
        metaGraphDef should contain(nodeDef2.id, Node(metaGraphDef, nodeDef2))
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
        val metaGraphDef = MetaGraphDef.withMembers(edgeDef1, edgeDef2)

        metaGraphDef should contain(edgeDef1.id, Edge(metaGraphDef, edgeDef1))
        metaGraphDef should contain(edgeDef2.id, Edge(metaGraphDef, edgeDef2))
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

        val metaGraphDef = MetaGraphDef.withMembers(
            nodeDef1,
            nodeDef2,
            edgeDef1,
            edgeDef2,
        )

        val node1 = metaGraphDef[nodeDef1.id]
        val node2 = metaGraphDef[nodeDef2.id]
        val edge1 = metaGraphDef[edgeDef1.id]
        val edge2 = metaGraphDef[edgeDef2.id]

        node1 shouldNot beNull()
        node2 shouldNot beNull()
        edge1 shouldNot beNull()
        edge2 shouldNot beNull()

        node1!!.incoming shouldBe emptyList()
        node1.outgoing should containOnly(Edge(metaGraphDef, edgeDef1))

        node2!!.incoming should containOnly(Edge(metaGraphDef, edgeDef1))
        node2.outgoing shouldBe emptyList()

        edge1!!.incoming should containOnly(Edge(metaGraphDef, edgeDef2))
        edge1.outgoing should containOnly(Edge(metaGraphDef, edgeDef2))
        (edge1 as Edge<String>).source shouldBe node1
        (edge1).target shouldBe node2

        edge2!!.incoming shouldBe emptyList()
        edge2.outgoing shouldBe emptyList()
        (edge2 as Edge<String>).source shouldBe edge1
        edge2.target shouldBe edge1
    }

    "MetaGraph should allow adding nodes" {
        val nodeDef1 = NodeDef<String>()
        val nodeDef2 = NodeDef<String>()
        val edgeDef1 = EdgeDef<String>(
            sourceRef = nodeDef1.id,
            targetRef = nodeDef2.id,
        )

        val metaGraphDef = MetaGraphDef<String>()

        val updated: MetaGraphDef<String> = metaGraphDef.apply {
            add(nodeDef1)
            add(nodeDef2)
            add(edgeDef1)
        }

        updated shouldContainValue Node(metaGraphDef, nodeDef1)
        updated shouldContainValue Node(metaGraphDef, nodeDef2)
        updated shouldContainValue Edge(metaGraphDef, edgeDef1)

        // and navigatino should be possible
        val node1 = updated[nodeDef1.id]!!
        val node2 = updated[nodeDef2.id]!!
        val edge1 = updated[edgeDef1.id]!! as Edge<String>

        node1.outgoing should containOnly(edge1)
        node2.incoming should containOnly(edge1)
        edge1.source shouldBe node1
        edge1.target shouldBe node2
    }
})