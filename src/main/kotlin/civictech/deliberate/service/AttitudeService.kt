package civictech.deliberate.service

import civictech.deliberate.domain.Attitude
import civictech.deliberate.domain.Histogram
import civictech.deliberate.repository.AttitudeRepository
import civictech.deliberate.repository.dto.AttitudeDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class AttitudeService(
    val attitudeRepository: AttitudeRepository
) {
    suspend fun setAttitudeHistogram(userName: String, contestableId: UUID, histogram: Histogram): Attitude {
        attitudeRepository.save(
            AttitudeDto(
                ownerName = userName,
                contestableId = contestableId,
//                histogramCenters = histogram.centers,
                histogramFractions = histogram.fractions
            )
        )
        return Attitude(
            userName,
            contestableId,
            histogram
        )
    }

}
