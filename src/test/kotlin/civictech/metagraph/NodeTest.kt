package civictech.metagraph

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

class NodeTest : StringSpec({
    "Node should allow default construction" {
        val nodeDef = NodeDef<String>()
        nodeDef.id shouldNot beNull()
        nodeDef.data should beNull()
    }
})