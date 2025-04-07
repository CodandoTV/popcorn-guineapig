package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.JustWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import org.junit.Test
import kotlin.test.assertEquals

class ArchitectureViolationErrorsExtTest {

    @Test
    fun `Given a list of errors when toErrorMessage is called then check the log message is right`() {
        // Arrange
        val listOfErrors = listOf(
            ArchitectureViolationError(
                message = "sample-data should not depends on presentation layer",
                rule = DoNotWithRule(listOf("presentation")),
                affectedRelationship = null
            ),
            ArchitectureViolationError(
                message = "sample-domain should not have deps",
                rule = NoDependencyRule(),
                affectedRelationship = null
            ),
            ArchitectureViolationError(
                message = "sample-presentation",
                rule = JustWithRule(justWith = listOf("domain", "presentation")),
                affectedRelationship = null
            )
        )

        // Act
        val errorMessage = listOfErrors.toErrorMessage()

        // Assert
        assertEquals(
            "sample-data should not depends on presentation layer\n" +
                    "sample-domain should not have deps\n" +
                    "sample-presentation", errorMessage
        )
    }
}
