@file:Suppress("MaxLineLength")
package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import kotlin.test.Test
import kotlin.test.assertEquals
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

    @Test
    fun `given design-system allowed only util_media but depends on util_logging feature-movies feature-list then rule fails and affectedRelationship contains unexpected deps`() {
        // Arrange
        val justWith = listOf("util_media")
        val justWithRule = JustWithRule(justWith)

        val deps = listOf(
            InternalDependenciesMetadata(group = null, moduleName = "util_logging"),
            InternalDependenciesMetadata(group = null, moduleName = "feature-movies"),
            InternalDependenciesMetadata(group = null, moduleName = "feature-list")
        )

        // Act
        val result = justWithRule.check(deps = deps)

        // Assert
        assertNotNull(result, "Rule should return an ArchitectureViolationError when unexpected deps are present")

        val affected = result.affectedRelationship
        assertNotNull(affected, "affectedRelationship must be present even if it's an empty list")
        val affectedNames = affected.map { it.moduleName }.toSet()

        // All three dependencies are unexpected (not in allowed list) — order is not asserted
        assertEquals(
            setOf("util_logging", "feature-movies", "feature-list"),
            affectedNames
        )
    }
}
