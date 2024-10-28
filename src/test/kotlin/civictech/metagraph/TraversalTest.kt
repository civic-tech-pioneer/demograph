package civictech.metagraph

import io.kotest.assertions.fail
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TraversalTest : StringSpec({

    "Empty MetaGraph should not visit anything" {
        val traversal = MetaGraph<String>().traversal()
        traversal.run {
            fail("No elements should be encountered: $it")
        }
    }

    "Singleton Node MetaGraph should visit just that node" {
        val nodeDef = NodeDef<String>()
        val metaGraph = MetaGraph(mapOf(nodeDef.id to nodeDef))
        val traversal = metaGraph.traversal()
        traversal.run {
            it shouldBe Node(metaGraph, nodeDef)
        }
    }
})