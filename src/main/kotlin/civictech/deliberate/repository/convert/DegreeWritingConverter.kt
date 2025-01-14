package civictech.deliberate.repository.convert

import civictech.deliberate.domain.Degree
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class DegreeWritingConverter : Converter<Degree, Float> {
    override fun convert(source: Degree): Float = source.value

}