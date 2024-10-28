package civictech.metagraph

import java.util.*

interface MemberDef<T> {
    val id: UUID
    val data: T?
}
