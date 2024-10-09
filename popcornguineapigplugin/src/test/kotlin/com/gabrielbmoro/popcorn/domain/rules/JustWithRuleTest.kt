package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class JustWithRuleTest  {

    @Test
    fun `given some not allowed dependencies when the rule checks then fails`() {
        // Arrange
        val justWith = listOf("data", "presentation")
        val justWithRule = JustWithRule(justWith)

        // Act
        val result = justWithRule.check(
            deps = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "data"
                )
            )
        )

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `given some allowed dependencies when the rule checks then fails`() {
        // Arrange
        val justWith = listOf("data", "presentation")
        val justWithRule = JustWithRule(justWith)

        // Act
        val result = justWithRule.check(
            deps = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "data"
                ),
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "presentation"
                ),
            )
        )

        // Assert
        assertNull(result)
    }
}
