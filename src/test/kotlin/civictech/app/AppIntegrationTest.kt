package civictech.app

import civictech.auth.TokenProvider
import civictech.auth.UserService
import civictech.deliberate.domain.Degree
import civictech.deliberate.graphql.datafetchers.AttitudeDataFetcher.Companion.toDomain
import civictech.dgs.DgsClient.buildMutation
import civictech.dgs.DgsClient.buildQuery
import civictech.dgs.types.*
import civictech.test.Codec
import civictech.test.DgsGraphQlClientTestConfig
import civictech.test.PostgresIntegrationTestConfiguration
import com.netflix.graphql.dgs.client.GraphQLClient
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.engine.runBlocking
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.web.client.HttpClientErrorException
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*
import java.util.function.Consumer
import civictech.deliberate.domain.Histogram as HistogramModel
import civictech.deliberate.domain.Bucket as BucketModel

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    classes = [
        PostgresIntegrationTestConfiguration::class,
        DgsGraphQlClientTestConfig::class,
    ],
)
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AppIntegrationTest {
    @MockitoBean
    private lateinit var httpHeadersConsumer: Consumer<HttpHeaders>

    @Autowired
    private lateinit var dgsGraphQLClient: GraphQLClient

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenProvider: TokenProvider

    private lateinit var token: String

    @BeforeAll
    fun setup() {
        runBlocking {
            userService.registerUser("user", "password")
        }
        token = tokenProvider.generate("user", listOf("USER"))
    }

    @Test
    fun `when creating a markdown node, then it should be retrievable`() {
        // Given some markdown
        val testMarkdown = "plain text"

        // When we create a mutation to store it as a markdown node and return the created id
        val addMarkdown: String = buildMutation(Codec) {
            addMarkdownNode(testMarkdown) {
                id
            }
        }
        // And we execute the mutation, and extract the id
        val result = dgsGraphQLClient.executeQuery(addMarkdown)
        result.errors shouldBe emptyList()

        val uuid: UUID = result.extractValueAsObject("addMarkdownNode.id", UUID::class.java)

        // When we create a query to retrieve the markdown node by id, and return its text
        val getNodeText = buildQuery(Codec) {
            markdownNode(uuid) {
                text
            }
        }
        // And we execute the query and extract the text
        val returnedMarkdown: String = dgsGraphQLClient
            .executeQuery(getNodeText)
            .extractValue("markdownNode.text")

        // Then the returned text ought to be the one we stored
        returnedMarkdown shouldBe testMarkdown
    }

    @Test
    fun `expressions allow updates`() {
        // Given a markdown node with text to improve
        val markdownNodeRef = withMarkdownNode("I'm a bad expression!")

        // When we update the node
        val updateNodeQuery = buildMutation(Codec) {
            updateMarkdownNode(markdownNodeRef, "I'm a good expression!") {
                id
                text
            }
        }
        val updatedMarkdownNode = dgsGraphQLClient
            .executeQuery(updateNodeQuery)
            .extractValueAsObject("updateMarkdownNode", MarkdownNode::class.java)

        // Then the link should be updated
        updatedMarkdownNode.id shouldBe markdownNodeRef
        updatedMarkdownNode.text shouldBe "I'm a good expression!"
    }

    @Test
    fun `when linking two markdown nodes, then the edge should be retrievable`() {
        // Given some markdown nodes
        val expression = withMarkdownNode("I'm an expression")
        val attack = withMarkdownNode("I'm an attacking argument")

        // When we create a link between them
        val createLinkQuery = buildMutation(Codec) {
            addLink(attack, expression) {
                id
                sourceRef
                targetRef
            }
        }
        val link = dgsGraphQLClient
            .executeQuery(createLinkQuery)
            .extractValueAsObject("addLink", Link::class.java)

        // Then the id should be a unique new UUID
        link.id shouldNotBeIn listOf(attack, expression)
        // and the from and to should be according to how it was supplied
        link.sourceRef shouldBe attack
        link.targetRef shouldBe expression
    }

    @Test
    fun `when linking two nodes, the nodes should have their source and target set appropriately`() {
        // Given some markdown nodes
        val expression = withMarkdownNode("I'm an expression")
        val attack = withMarkdownNode("I'm an attacking argument")

        // When we create a link between them
        val createLinkQuery = buildMutation(Codec) {
            addLink(attack, expression) {
                id
                sourceRef
                targetRef
            }
        }
        val link = dgsGraphQLClient
            .executeQuery(createLinkQuery)
            .extractValueAsObject("addLink", Link::class.java)

        val linkedExpression = getMarkdownNode(expression)
        val linkedAttack = getMarkdownNode(attack)

        linkedAttack.sourceLinks.map(Link::id) shouldContainExactly listOf(link.id)
        linkedAttack.targetLinks shouldBe emptyList()
        linkedExpression.targetLinks.map(Link::id) shouldContainExactly listOf(link.id)
        linkedExpression.sourceLinks shouldBe emptyList()
    }

    @Test
    fun `graphql queries with invalid credentials should not be authorized`() {
        modifyHeaders { it.setBearerAuth("I'm not a valid token") }

        val error = shouldThrow<HttpClientErrorException> { withMarkdownNode("This text is irrelevant") }

        error.statusCode shouldBe HttpStatus.UNAUTHORIZED
    }

    @Test
    fun `agents can provide their attitude towards nodes`() {
        modifyHeaders { it.setBearerAuth(token) }

        val markdownNodeRef: UUID = withMarkdownNode("I'm a highly controversial statement")

        val histogram = HistogramInput(listOf(BucketInput(1.0, 1.0)))
        val createAttitude = buildMutation(Codec) {
            setAttitudeHistogram(markdownNodeRef, histogram) {
                agent {
                    userName
                }
                contestableId
                histogram {
                    buckets {
                        center
                        value
                    }
                }
            }
        }

        val result = dgsGraphQLClient
            .executeQuery(createAttitude)
            .extractValueAsObject("setAttitudeHistogram", Attitude::class.java)

        result shouldNotBe null
        result.agent.userName shouldBe "user"
        result.contestableId shouldBe markdownNodeRef

        result.histogram?.toDomain() shouldBe histogram.toDomain()
    }

    private fun modifyHeaders(consumer: Consumer<HttpHeaders>) {
        val argumentCaptor = ArgumentCaptor.forClass(HttpHeaders::class.java)
        `when`(httpHeadersConsumer.accept(argumentCaptor.capture())).then {
            consumer.accept(argumentCaptor.value)
        }
    }

    private fun withMarkdownNode(text: String): UUID {
        val addMarkdown: String = buildMutation(Codec) {
            addMarkdownNode(text) {
                id
            }
        }
        // And we execute the mutation, and extract the id
        val result = dgsGraphQLClient.executeQuery(addMarkdown)
        return result.extractValueAsObject("addMarkdownNode.id", UUID::class.java)
    }

    private fun getMarkdownNode(uuid: UUID): MarkdownNode = dgsGraphQLClient
        .executeQuery(buildQuery(Codec) {
            markdownNode(uuid) {
                id
                text
                sourceLinks {
                    id
                    sourceRef
                    targetRef
                }
                targetLinks {
                    id
                    sourceRef
                    targetRef
                }
                owner {
                    userName
                }
            }
        })
        .extractValueAsObject("markdownNode", MarkdownNode::class.java)

    private fun Histogram.toDomain(): HistogramModel =
        HistogramModel(buckets = this.buckets.map { it.toDomain() })

    private fun Bucket.toDomain(): BucketModel = BucketModel(
        Degree.of(this.center),
        Degree.of(this.value)
    )
}
