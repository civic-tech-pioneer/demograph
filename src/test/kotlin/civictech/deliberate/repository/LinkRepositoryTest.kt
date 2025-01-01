package civictech.deliberate.repository

import civictech.deliberate.repository.dto.LinkDTO
import civictech.test.PostgresIntegrationTestConfiguration
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(
    classes = [
        PostgresIntegrationTestConfiguration::class,
    ]
)
@DataR2dbcTest
class LinkRepositoryTest {

    @Autowired
    lateinit var linkRepo: LinkRepository

    @Test
    fun `LinkRepository should retrieve links by sourceRefs`() = runTest {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val uuid3 = UUID.randomUUID()
        val targetUUID = UUID.randomUUID()

        linkRepo.save(LinkDTO(sourceRef = uuid1, targetRef = targetUUID))
        linkRepo.save(LinkDTO(sourceRef = uuid2, targetRef = targetUUID))
        linkRepo.save(LinkDTO(sourceRef = uuid3, targetRef = targetUUID))

        val links = linkRepo.findBySourceRefIn(setOf(uuid1, uuid2))

        links shouldHaveSize 2

        links.map(LinkDTO::sourceRef) shouldContainExactly listOf(uuid1, uuid2)
    }
}