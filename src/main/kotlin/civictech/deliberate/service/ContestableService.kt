package civictech.deliberate.service

import civictech.deliberate.domain.Link
import civictech.deliberate.domain.MarkdownNode
import civictech.deliberate.repository.LinkRepository
import civictech.deliberate.repository.MarkdownNodeRepository
import civictech.deliberate.repository.dto.LinkDTO
import civictech.deliberate.repository.dto.MarkdownNodeDTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ContestableService(
    private val markdownNodeRepository: MarkdownNodeRepository,
    private val linkRepository: LinkRepository,
) {
    suspend fun getMarkdownNode(id: UUID): MarkdownNode? =
        markdownNodeRepository.findById(id)?.let(::toDomainModel)

    suspend fun addMarkdownNode(text: String): MarkdownNode =
        markdownNodeRepository.save(MarkdownNodeDTO(text = text)).let(::toDomainModel)

    @Transactional
    suspend fun updateMarkdownNode(id: UUID, text: String): MarkdownNode? {
        return markdownNodeRepository.findById(id)
            ?.copy(text = text)
            ?.also { markdownNodeRepository.save(it) }
            ?.let(::toDomainModel)
    }

    suspend fun getLink(id: UUID): Link? =
        linkRepository.findById(id)?.let(::toDomainModel)

    suspend fun addLink(from: UUID, to: UUID): Link = linkRepository.save(
        LinkDTO(
            sourceRef = from,
            targetRef = to
        )
    ).let(::toDomainModel)

    private fun toDomainModel(it: MarkdownNodeDTO) = MarkdownNode(it.id, it.text)
    private fun toDomainModel(it: LinkDTO) = Link(it.id, it.sourceRef, it.targetRef)
}
