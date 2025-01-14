package civictech.deliberate.graphql.datafetchers

import civictech.deliberate.domain.Bucket
import civictech.deliberate.domain.Degree
import civictech.deliberate.service.AttitudeService
import civictech.deliberate.domain.Histogram
import civictech.dgs.types.Agent
import civictech.dgs.types.Attitude
import civictech.dgs.types.BucketInput
import civictech.dgs.types.HistogramInput
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
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

    companion object {
        fun HistogramInput.toDomain(): Histogram = Histogram(this.buckets.map { it.toDomain() })
        fun BucketInput.toDomain(): Bucket = Bucket(Degree.of(this.center), Degree.of(this.value))
        fun civictech.deliberate.domain.Attitude.toApi(): Attitude = Attitude(
            { Agent { this.agentName } },
            { this.target },
            { this.attitude.toApi() }
        )

        fun Histogram.toApi() = HistogramOutput({
            this.buckets.map { it.toApi() }
        })

        fun Bucket.toApi() =
            BucketOutput({ this.center.value.toDouble() }, { this.fraction.value.toDouble() })
    }
}

