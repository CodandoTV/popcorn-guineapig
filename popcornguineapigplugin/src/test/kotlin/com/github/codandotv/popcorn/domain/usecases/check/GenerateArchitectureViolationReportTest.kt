package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepository
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithError
import org.junit.Test
import kotlin.test.assertFails

class GenerateArchitectureViolationReportTest {

    private lateinit var generateArchitectureViolationReport: GenerateArchitectureViolationReport
    private val fakeCheckResult = CheckResult.Failure(
        errors = listOf(
            ArchitectureViolationError(
                message = "no dependency rule",
                affectedRelationship = null,
                rule = NoDependencyRule(),
            )
        )
    )

    @Test
    fun `Given a report object when generate report is called then checks if all flow runs`() {
        // arrange
        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(fakePopcornGuineapigRepository)

        // act
        generateArchitectureViolationReport.execute(
            reportPath = "",
            results = mapOf(
                "moduleName" to fakeCheckResult
            )
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `Given a report object + an exception in the repository when generate report is called then checks if the exception is being triggered`() {
        // arrange
        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(fakePopcornGuineapigRepositoryWithError)

        // act, assert
        assertFails {
            generateArchitectureViolationReport.execute(
                reportPath = "",
                results = mapOf(
                    "moduleName" to fakeCheckResult
                )
            )
        }
    }
}
