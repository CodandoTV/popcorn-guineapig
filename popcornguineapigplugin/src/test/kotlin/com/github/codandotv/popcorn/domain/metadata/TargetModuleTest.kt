package com.github.codandotv.popcorn.domain.metadata

import com.github.codandotv.popcorn.domain.models.TargetModule
import kotlin.test.Test
import kotlin.test.assertEquals

class TargetModuleTest {

    @Test
    fun `Given a target module when logMessage is called then returns a formatted message`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "feature-data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "design-system"
                ),
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "core-system"
                ),
                InternalDependenciesMetadata(
                    group = "PopcornTest",
                    moduleName = "util"
                )
            ),
            rules = emptyList(),
        )

        // Act
        val logMessage = targetModule.toString()

        // Assert
        assertEquals(
            "\n----------------------\n" +
                    "Module name: feature-data\n" +
                    "Internal dependencies: design-system,core-system,util\n" +
                    "----------------------", logMessage
        )
    }
}
