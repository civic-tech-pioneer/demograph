package civictech.deliberate.repository.dto

import civictech.deliberate.domain.Degree
import civictech.deliberate.domain.Histogram
import civictech.deliberate.domain.HistogramDef
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table(name = "attitudes")
data class AttitudeDto(
    @Id val id: UUID = UUID.randomUUID(),
    @Version val version: Int? = null,
    val ownerName: String,
    val contestableId: UUID,
//    val histogramCenters: List<Degree>,
    val histogramFractions: List<Degree>
) {
    fun histogram(): Histogram =
        HistogramDef.of(histogramFractions.size).populateIndexed { histogramFractions[it] }
}
