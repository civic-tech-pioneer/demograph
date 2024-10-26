package civictech.metagraph

import java.util.UUID

abstract class Member<Data> {
    abstract val metaGraph: MetaGraph<Data>
    abstract val id: UUID
    abstract val data: Data?
}
