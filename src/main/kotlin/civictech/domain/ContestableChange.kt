package civictech.domain

import civictech.deliberate.domain.Histogram
import java.time.Instant
import java.util.*

data class ContestableChange(
    val sourceId: UUID,
    val sourceType: SourceType,
    val changeType: ChangeType,
    val old: Histogram? = null,
    val new: Histogram? = null,
    val time: Instant = Instant.now(),
)
