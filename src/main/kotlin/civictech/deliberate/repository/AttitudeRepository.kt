package civictech.deliberate.repository

import civictech.deliberate.repository.dto.AttitudeDto
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AttitudeRepository : CoroutineCrudRepository<AttitudeDto, UUID> {
    suspend fun findAllByContestableId(contestableId: UUID): List<AttitudeDto>
}
