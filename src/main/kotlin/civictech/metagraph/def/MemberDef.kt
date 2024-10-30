package civictech.metagraph.def

import civictech.metagraph.Quantifiable
import java.util.*

abstract class MemberDef<In, Out : Quantifiable> {
    private var _data: In? = null
    private var _integrated: Out? = null

    abstract val id: UUID
    abstract val initialData: In?

    var data: In?
        get() = _data
        internal set(value) {
            _data = value
        }

    var integrated: Out?
        get() = _integrated
        internal set(value) {
            _integrated = value
        }
}

