package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DoNotWithRuleTest {

    @Test
    fun `given some not allowed dependencies when the rule checks then fails`() {
        // Arrange
        val doNotWith = listOf("data", "presentation")
        val doNotWithRule = DoNotWithRule(doNotWith)

        // Act
        val result = doNotWithRule.check(
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
        val doNotWith = listOf("data")
        val doNotWithRule = DoNotWithRule(doNotWith)

        // Act
        val result = doNotWithRule.check(
            deps = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "presentation"
                )
            )
        )

        // Assert
        assertNull(result)
    }
}
