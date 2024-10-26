package civictech.metagraph

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.beEmpty
import io.kotest.matchers.maps.contain
import io.kotest.matchers.should
import java.util.*

class MetaGraphTest : StringSpec({
    "MetaGraph should be initialized empty" {
        MetaGraph<String>() should beEmpty()
    }

    "MetaGraph should allow providing nodes" {
        val nodeDef1 = NodeDef<String>()
        val nodeDef2 = NodeDef<String>()
        val metaGraph = MetaGraph(mapOf(nodeDef1.id to nodeDef1, nodeDef2.id to nodeDef2))

        metaGraph should contain(nodeDef1.id, Node(metaGraph, nodeDef1))
        metaGraph should contain(nodeDef2.id, Node(metaGraph, nodeDef2))
    }

    "MetaGraph should allow providing edges" {
        val edgeDef1 = EdgeDef<Unit>(
            fromRef = UUID.randomUUID(),
            toRef = UUID.randomUUID(),
        )
        val edgeDef2 = EdgeDef<Unit>(
            fromRef = UUID.randomUUID(),
            toRef = UUID.randomUUID(),
        )
        val metaGraph = MetaGraph(mapOf(), mapOf(edgeDef1.id to edgeDef1, edgeDef2.id to edgeDef2))

        metaGraph should contain(edgeDef1.id, Edge(metaGraph, edgeDef1))
        metaGraph should contain(edgeDef2.id, Edge(metaGraph, edgeDef2))
    }
})