@file:Suppress("MaxLineLength")
package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun `given domain marked with doNotWith and depends on feature_list and feature_movies then rule fails and affectedRelationship contains both`() {
        // Arrange
        val doNotWith = listOf("feature_list", "feature_movies")
        val doNotWithRule = DoNotWithRule(doNotWith)

        val deps = listOf(
            InternalDependenciesMetadata(group = null, moduleName = "feature_list"),
            InternalDependenciesMetadata(group = null, moduleName = "feature_movies")
        )

        // Act
        val result = doNotWithRule.check(deps = deps)

        // Assert
        assertNotNull(result, "Rule should produce an ArchitectureViolationError when forbidden deps are present")

        val affected = result.affectedRelationship
        assertNotNull(affected)
        val affectedNames = affected.map { it.moduleName }.toSet()

        assertEquals(
            setOf("feature_list", "feature_movies"),
            affectedNames,
            "Both forbidden dependencies must be reported in affectedRelationship"
        )
    }
}
