package civictech.deliberate.repository

import civictech.deliberate.repository.dto.LinkDTO
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface LinkRepository : CoroutineCrudRepository<LinkDTO, UUID> {
    suspend fun findBySourceRef(sourceRef: UUID): List<LinkDTO>
    suspend fun findBySourceRefIn(sourceRefs: Collection<UUID>): List<LinkDTO>
    suspend fun findByTargetRef(targetRef: UUID): List<LinkDTO>
    suspend fun findByTargetRefIn(targetRef: Collection<UUID>): List<LinkDTO>
}
