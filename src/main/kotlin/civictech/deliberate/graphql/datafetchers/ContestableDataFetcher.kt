package civictech.deliberate.graphql.datafetchers

import civictech.deliberate.graphql.asDgsType
import civictech.deliberate.service.ContestableService
import civictech.dgs.types.Link
import civictech.dgs.types.MarkdownNode
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.future.await
import org.dataloader.DataLoader
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

    @DgsData(parentType = "MarkdownNode", field = "sourceLinks")
    suspend fun markdownSourceLinks(dfe: DataFetchingEnvironment): List<Link> {
        val sourceLinksLoader: DataLoader<UUID, List<Link>?>? = dfe.getDataLoader("sourceLinks")
        val id = dfe.getSource<MarkdownNode>()?.id

        if (id == null || sourceLinksLoader == null) {
            return listOf()
        }

        return sourceLinksLoader.load(id).await() ?: listOf()
    }

    @DgsData(parentType = "MarkdownNode", field = "targetLinks")
    suspend fun markdownTargetLinks(dfe: DataFetchingEnvironment): List<Link> {
        val targetLinksLoader: DataLoader<UUID, List<Link>?>? = dfe.getDataLoader("targetLinks")
        val id = dfe.getSource<MarkdownNode>()?.id

        if (id == null || targetLinksLoader == null) {
            return listOf()
        }

        return targetLinksLoader.load(id).await() ?: listOf()
    }
}