package civictech.app

import civictech.auth.TokenProvider
import civictech.auth.UserService
import civictech.deliberate.domain.*
import civictech.deliberate.domain.Degree.Companion.ONE
import civictech.deliberate.domain.Degree.Companion.ZERO
import civictech.deliberate.domain.Degree.Companion.toDegree
import civictech.deliberate.domain.Histogram.Companion.approxEquals
import civictech.deliberate.graphql.datafetchers.AttitudeDataFetcher.Companion.toDomain
import civictech.deliberate.repository.convert.ConversionConfig
import civictech.dgs.DgsClient.buildMutation
import civictech.dgs.DgsClient.buildQuery
import civictech.dgs.types.Attitude
import civictech.dgs.types.BucketInput
import civictech.dgs.types.HistogramInput
import civictech.dgs.types.Link
import civictech.dgs.types.MarkdownNode
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.reset
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
import civictech.dgs.types.Bucket as BucketOutput
import civictech.dgs.types.Histogram as HistogramOutput

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

    private val user1: String = "user1"
    private val user2: String = "user2"

    @BeforeAll
    fun setup() {
        runBlocking {
            coroutineScope {
                listOf(
                    async(IO) { userService.registerUser(user1, "password") },
                    async(IO) { userService.registerUser(user2, "password") }
                ).awaitAll()
            }
        }
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
        modifyHeaders { it.setBearerAuth(tokenProvider.generate(user1)) }

        val markdownNodeRef: UUID = withMarkdownNode("I'm a highly controversial statement")

        val histogram = HistogramInput(listOf(BucketInput(1.0)))
        val createAttitude = buildMutation(Codec) {
            setAttitudeHistogram(markdownNodeRef, histogram) {
                agent {
                    userName
                }
                contestableId
                histogram {
                    buckets {
                        value
                    }
                }
            }
        }

        val result = dgsGraphQLClient
            .executeQuery(createAttitude)
            .extractValueAsObject("setAttitudeHistogram", Attitude::class.java)

        result shouldNotBe null
        result.agent.userName shouldBe "user1"
        result.contestableId shouldBe markdownNodeRef

        result.histogram?.toDomain() shouldBe histogram.toDomain()
    }

    @Test
    fun `providing attitudes eventually updates the direct average attitude`() {
        val markdownNodeRef: UUID = withMarkdownNode("I'm a highly controversial statement")

        val disbeliever = HistogramDef.DEFAULT.distribution(ZERO, Confidence.FULL)
        val believer = HistogramDef.DEFAULT.distribution(ONE, Confidence.FULL)
        setAttitudeHistogram(user1, markdownNodeRef, disbeliever)
        setAttitudeHistogram(user2, markdownNodeRef, believer)

        val markdownNode = getMarkdownNode(markdownNodeRef)

        markdownNode.averageAttitude shouldNotBe null

        val updatedAttitude = markdownNode.averageAttitude!!.toDomain()
        val expectedAttitude = SimpleHistogram.arithmeticMean(disbeliever, believer)
        updatedAttitude.approxEquals(expectedAttitude) shouldBe true
    }

    private fun setAttitudeHistogram(user: String, element: UUID, histogram: Histogram): Attitude {
        modifyHeaders { it.setBearerAuth(tokenProvider.generate(user)) }
        val createAttitude = buildMutation(Codec) {
            setAttitudeHistogram(element, histogram.toInput()) {
                agent {
                    userName
                }
                contestableId
                histogram {
                    buckets {
                        value
                    }
                }
            }
        }
        return dgsGraphQLClient
            .executeQuery(createAttitude)
            .extractValueAsObject("setAttitudeHistogram", Attitude::class.java)
    }

    private fun modifyHeaders(consumer: Consumer<HttpHeaders>) {
        val argumentCaptor = ArgumentCaptor.forClass(HttpHeaders::class.java)
        reset(httpHeadersConsumer)
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
                averageAttitude {
                    buckets {
                        value
                    }
                }
            }
        })
        .extractValueAsObject("markdownNode", MarkdownNode::class.java)

    private fun HistogramOutput.toDomain(): Histogram =
        SimpleHistogram.of(buckets = this.buckets.mapIndexed { i, bo -> bo.toDomain(this.buckets.size, i) })!!

    private fun BucketOutput.toDomain(bucketCount: Int, i: Int): Bucket =
        Bucket.of(bucketCount, i, value.toDegree())

    private fun Histogram.toInput(): HistogramInput =
        HistogramInput(buckets = buckets.map { it.toInput() })

    private fun Bucket.toInput(): BucketInput =
        BucketInput(value.value)
}
