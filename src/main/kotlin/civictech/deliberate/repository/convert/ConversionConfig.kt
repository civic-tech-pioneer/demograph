package civictech.deliberate.repository.convert

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect

@Configuration
class ConversionConfig {

    @Bean
    fun customConversions(): R2dbcCustomConversions = R2dbcCustomConversions.of(
        PostgresDialect.INSTANCE, listOf(
            DegreeReadingConverter(),
            DegreeWritingConverter()
        )
    )
}