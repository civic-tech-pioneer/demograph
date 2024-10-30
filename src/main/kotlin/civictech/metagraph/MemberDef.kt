package civictech.metagraph

import java.util.*

interface MemberDef<In> {
    val id: UUID
    var data: In?
}
