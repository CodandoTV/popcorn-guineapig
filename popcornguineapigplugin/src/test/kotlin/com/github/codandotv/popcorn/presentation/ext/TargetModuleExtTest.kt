package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import kotlin.test.Test
import kotlin.test.assertEquals

class TargetModuleExtTest {

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
            )
        )

        // Act
        val logMessage = targetModule.logMessage()

        // Assert
        assertEquals(
            "\n----------------------\n" +
                    "Module name: feature-data\n" +
                    "Internal dependencies: design-system,core-system,util\n" +
                    "----------------------", logMessage
        )
    }
}
