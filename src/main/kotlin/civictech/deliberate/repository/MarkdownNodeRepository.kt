package civictech.deliberate.repository

import civictech.deliberate.repository.dto.MarkdownNodeDTO
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface MarkdownNodeRepository : CoroutineCrudRepository<MarkdownNodeDTO, UUID>
