package civictech.deliberate.repository.convert

import civictech.deliberate.domain.Degree
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class DegreeReadingConverter : Converter<Double, Degree> {
    override fun convert(source: Double): Degree? = Degree.orNull(source)
}