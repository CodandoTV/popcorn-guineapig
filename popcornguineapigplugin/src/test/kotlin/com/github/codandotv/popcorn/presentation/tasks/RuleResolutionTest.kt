package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RuleResolutionTest {

    @Test
    fun `Given overlapping child configurations when resolved then every matching rule is kept`() {
        // Arrange
        val noDependency = NoDependencyRule()
        val doNotWith = DoNotWithRule(notWith = listOf("data"))
        val children = listOf(
            PopcornChildConfiguration(moduleNameRegex = ":.*", rules = listOf(noDependency)),
            PopcornChildConfiguration(moduleNameRegex = ":car:data", rules = listOf(doNotWith)),
        )

        // Act
        val result = resolveRulesByProjectPath(children, listOf(":car:data", ":app"))

        // Assert
        assertEquals(setOf(":car:data", ":app"), result.keys)
        assertEquals(listOf(noDependency, doNotWith), result[":car:data"])
        assertEquals(listOf(noDependency), result[":app"])
    }

    @Test
    fun `Given a rule shared by two matching configurations when resolved then it is not duplicated`() {
        // Arrange
        val sharedRule = NoDependencyRule()
        val children = listOf(
            PopcornChildConfiguration(moduleNameRegex = ":.*", rules = listOf(sharedRule)),
            PopcornChildConfiguration(moduleNameRegex = ":car:.*", rules = listOf(sharedRule)),
        )

        // Act
        val result = resolveRulesByProjectPath(children, listOf(":car:data"))

        // Assert
        assertEquals(listOf(sharedRule), result[":car:data"])
    }

    @Test
    fun `Given a project without matching configuration when resolved then it is not evaluated`() {
        // Arrange
        val children = listOf(
            PopcornChildConfiguration(moduleNameRegex = ":util:.*", rules = listOf(NoDependencyRule())),
        )

        // Act
        val result = resolveRulesByProjectPath(children, listOf(":app", ":car:data"))

        // Assert
        assertTrue(result.isEmpty())
    }
}
