package civictech.app

import civictech.dgs.DgsClient.buildMutation
import civictech.dgs.DgsClient.buildQuery
import civictech.dgs.types.Link
import civictech.dgs.types.MarkdownNode
import civictech.test.Codec
import civictech.test.DgsGraphQlClientTestConfig
import civictech.test.PostgresIntegrationTestConfiguration
import com.netflix.graphql.dgs.client.GraphQLClient
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
    classes = [
        PostgresIntegrationTestConfiguration::class,
        DgsGraphQlClientTestConfig::class,
    ],
)
@Testcontainers
class AppIntegrationTest {
    @Autowired
    private lateinit var dgsGraphQLClient: GraphQLClient

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
            }
        })
        .extractValueAsObject("markdownNode", MarkdownNode::class.java)
}