package civictech.domain

import civictech.deliberate.domain.SimpleHistogram
import java.time.Instant
import java.util.*

data class ContestableChange(
    val sourceId: UUID,
    val sourceType: SourceType,
    val changeType: ChangeType,
    val old: SimpleHistogram? = null,
    val new: SimpleHistogram? = null,
    val time: Instant = Instant.now(),
)
