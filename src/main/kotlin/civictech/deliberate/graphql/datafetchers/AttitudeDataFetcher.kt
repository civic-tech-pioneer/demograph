package civictech.deliberate.graphql.datafetchers

import civictech.deliberate.domain.Bucket
import civictech.deliberate.domain.Degree.Companion.toDegree
import civictech.deliberate.domain.Histogram
import civictech.deliberate.domain.HistogramDef
import civictech.deliberate.domain.SimpleHistogram
import civictech.deliberate.service.AttitudeService
import civictech.dgs.types.Agent
import civictech.dgs.types.Attitude
import civictech.dgs.types.Contestable
import civictech.dgs.types.HistogramInput
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsData
import com.netflix.graphql.dgs.DgsMutation
import graphql.schema.DataFetchingEnvironment
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import java.util.*
import civictech.dgs.types.Bucket as BucketOutput
import civictech.dgs.types.Histogram as HistogramOutput

@DgsComponent
class AttitudeDataFetcher(
    private val attitudeService: AttitudeService
) {

    @DgsMutation
    @PreAuthorize("isAuthenticated()")
    suspend fun setAttitudeHistogram(on: UUID, histogram: HistogramInput): Attitude {
        val principal: Authentication =
            ReactiveSecurityContextHolder.getContext().map { it.authentication }.awaitSingle()
        return attitudeService.setAttitudeHistogram(principal.name, on, histogram.toDomain()).toApi()
    }

    @DgsData(parentType = "Contestable")
    @PreAuthorize("isAuthenticated()")
    suspend fun averageAttitude(dfe: DataFetchingEnvironment): HistogramOutput? {
        return dfe.getSource<Contestable>()
            ?.id
            ?.let { attitudeService.getAverageAttitude(it) }
            ?.toApi()
    }

    companion object {
        fun HistogramInput.toDomain(): SimpleHistogram = SimpleHistogram.of(
            HistogramDef.of(this.buckets.size),
            this.buckets.map { it.value.toDegree() }
        )

        fun Histogram.toApi(): HistogramOutput = HistogramOutput({ this.buckets.map { it.toApi() } })

        fun civictech.deliberate.domain.Attitude.toApi(): Attitude = Attitude(
            { Agent { this.agentName } },
            { this.target },
            { this.attitude.toApi() }
        )

        fun SimpleHistogram.toApi() = HistogramOutput({
            this.buckets.map { it.toApi() }
        })

        fun Bucket.toApi() =
            BucketOutput({ this.value.value })
    }
}

