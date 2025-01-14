package civictech.domain

import java.util.UUID

interface ContestableUpdater {
    fun update(contestableId: UUID, changes: List<ContestableChange>)
}