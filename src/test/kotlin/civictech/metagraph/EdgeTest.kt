package civictech.metagraph

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.util.*

class EdgeTest : StringSpec({
    "Edge should allow default initialization" {
        val edgeDef = EdgeDef<String>(
            fromRef = UUID.randomUUID(),
            toRef = UUID.randomUUID()
        )
        edgeDef.id shouldNot beNull()
        edgeDef.fromRef shouldNot beNull()
        edgeDef.toRef shouldNot beNull()
        edgeDef.data should beNull()
    }

    "Edge should not allow self-connection" {
        val id = UUID.randomUUID()
        shouldThrow<IllegalArgumentException> {
            EdgeDef<String>(id, fromRef = id, toRef = UUID.randomUUID())
        }
        shouldThrow<IllegalArgumentException> {
            EdgeDef<String>(id, fromRef = UUID.randomUUID(), toRef = id)
        }
    }
})