package civictech.metagraph

import civictech.metagraph.def.EdgeDef
import civictech.test.Null
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.util.*

class EdgeTest : StringSpec({
    "Edge should allow default initialization" {
        val edgeDef = EdgeDef<Null, Null>(
            sourceRef = UUID.randomUUID(),
            targetRef = UUID.randomUUID()
        )
        edgeDef.id shouldNot beNull()
        edgeDef.sourceRef shouldNot beNull()
        edgeDef.targetRef shouldNot beNull()
        edgeDef.data should beNull()
    }

    "Edge should not allow self-connection" {
        val id = UUID.randomUUID()
        shouldThrow<IllegalArgumentException> {
            EdgeDef<Null, Null>(id, sourceRef = id, targetRef = UUID.randomUUID())
        }
        shouldThrow<IllegalArgumentException> {
            EdgeDef<Null, Null>(id, sourceRef = UUID.randomUUID(), targetRef = id)
        }
    }

})