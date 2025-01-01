package civictech.dto

import civictech.domain.agentArb
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.of
import io.kotest.property.forAll

private val jsonMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
private val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule.Builder().build())
private val mappers: Exhaustive<ObjectMapper> = Exhaustive.of(jsonMapper, yamlMapper)

class SerializationTest : StringSpec({
    "AgentDef should serialize to json or yaml and back".config(enabled = false) {
        forAll(mappers, agentArb) { mapper, agentDef ->
            agentDef == mapper.readValue<AgentDef>(mapper.writeValueAsString(agentDef))
        }
    }
})
