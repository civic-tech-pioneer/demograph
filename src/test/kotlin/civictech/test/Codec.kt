package civictech.test

import com.netflix.graphql.dgs.client.codegen.InputValueSerializer
import graphql.scalars.ExtendedScalars
import java.util.*

object Codec : InputValueSerializer(
    mapOf(
        UUID::class.java to ExtendedScalars.UUID.coercing
    )
)