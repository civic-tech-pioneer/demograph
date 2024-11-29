package civictech.deliberate.repository

import civictech.deliberate.repository.dto.LinkDTO
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface LinkRepository : CoroutineCrudRepository<LinkDTO, UUID>
