package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NoDependencyRuleTest  {

    @Test
    fun `given a module with some deps when the no dependency rules is called then fails`() {
        // Arrange
        val noDependencyRule = NoDependencyRule()

        // Act
        val result = noDependencyRule.check(
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
    fun `given a module with no deps when the no dependency rules is called then passes`() {
        // Arrange
        val noDependencyRule = NoDependencyRule()

        // Act
        val result = noDependencyRule.check(
            deps = emptyList()
        )

        // Assert
        assertNull(result)
    }
}
