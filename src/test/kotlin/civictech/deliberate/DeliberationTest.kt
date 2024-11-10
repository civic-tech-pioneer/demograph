package civictech.deliberate

import io.kotest.core.spec.style.StringSpec

class DeliberationTest : StringSpec({
    "An empty Deliberation can be instantiated" {
        val deliberation = Deliberation()
    }

    "A Deliberation can be created with some arguments" {
        val deliberation = Deliberation.from()
    }
})