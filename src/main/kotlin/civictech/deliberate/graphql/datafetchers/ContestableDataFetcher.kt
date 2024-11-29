package civictech.deliberate.graphql.datafetchers

import civictech.deliberate.service.ContestableService
import civictech.dgs.types.Link
import civictech.dgs.types.MarkdownNode
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import java.util.*

@DgsComponent
class ContestableDataFetcher(
    private val contestableService: ContestableService,
) {

    @DgsQuery
    suspend fun markdownNode(id: UUID): MarkdownNode? =
        contestableService.getMarkdownNode(id)?.let(::asDgsType)

    @DgsMutation
    suspend fun addMarkdownNode(text: String): MarkdownNode =
        contestableService.addMarkdownNode(text).let(::asDgsType)

    @DgsMutation
    suspend fun updateMarkdownNode(id: UUID, text: String): MarkdownNode? =
        contestableService.updateMarkdownNode(id, text)?.let(::asDgsType)

    @DgsQuery
    suspend fun link(id: UUID): Link? =
        contestableService.getLink(id)?.let(::asDgsType)

    @DgsMutation
    suspend fun addLink(from: UUID, to: UUID): Link =
        contestableService.addLink(from, to).let(::asDgsType)

    private fun asDgsType(it: civictech.deliberate.domain.MarkdownNode) =
        MarkdownNode({ it.id }, { it.text })

    private fun asDgsType(it: civictech.deliberate.domain.Link) =
        Link({ it.id }, { it.sourceRef }, { it.targetRef })
}