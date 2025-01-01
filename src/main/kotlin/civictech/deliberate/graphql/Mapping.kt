package civictech.deliberate.graphql

import civictech.dgs.types.Link
import civictech.dgs.types.MarkdownNode

fun asDgsType(it: civictech.deliberate.domain.MarkdownNode) =
    MarkdownNode({ it.id }, { it.text })

fun asDgsType(it: civictech.deliberate.domain.Link) =
    Link({ it.id }, { it.sourceRef }, { it.targetRef })