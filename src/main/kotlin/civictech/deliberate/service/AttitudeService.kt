package civictech.deliberate.service

import civictech.deliberate.domain.Attitude
import civictech.deliberate.domain.Histogram
import civictech.deliberate.domain.SimpleHistogram
import civictech.deliberate.domain.SimpleHistogram.Companion.arithmeticMean
import civictech.deliberate.repository.AttitudeRepository
import civictech.deliberate.repository.dto.AttitudeDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class AttitudeService(
    val attitudeRepository: AttitudeRepository
) {
    suspend fun setAttitudeHistogram(userName: String, contestableId: UUID, histogram: SimpleHistogram): Attitude {
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

    suspend fun getAverageAttitude(contestableId: UUID): Histogram? {
        val findAllByContestableId: List<AttitudeDto> = attitudeRepository.findAllByContestableId(contestableId)
        val histograms = findAllByContestableId.map { it.histogram() }
        return arithmeticMean(histograms)
    }

}
