package civictech.deliberate.graphql.dataloaders

import civictech.deliberate.graphql.asDgsType
import civictech.deliberate.service.ContestableService
import civictech.dgs.types.Link
import com.netflix.graphql.dgs.DgsDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.future
import org.dataloader.MappedBatchLoader
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

@DgsDataLoader(name = "sourceLinks")
class SourceLinksDataLoader(
    private val contestableService: ContestableService
) : MappedBatchLoader<UUID, List<Link>> {

    override fun load(keys: MutableSet<UUID>?): CompletionStage<MutableMap<UUID, List<Link>>> {
        if (keys == null) {
            return CompletableFuture.completedFuture(mutableMapOf())
        }

        return CoroutineScope(Dispatchers.Default).future {
            contestableService.getLinksBySourceRefs(keys)
                .mapValues { entry -> entry.value.map(::asDgsType) }
                .toMutableMap()
        }
    }
}