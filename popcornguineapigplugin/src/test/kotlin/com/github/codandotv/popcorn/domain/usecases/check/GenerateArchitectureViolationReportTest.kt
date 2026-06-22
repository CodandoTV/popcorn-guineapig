package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.fakes.FakeLogger
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepository
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithError
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertTrue

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
        val logMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(logMessages, errorMessages)

        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(fakeLogger, fakePopcornGuineapigRepository)

        generateArchitectureViolationReport.execute(
            reportPath = "some/path",
            results = mapOf(
                "moduleName" to fakeCheckResult
            )
        )

        assertContains(logMessages.first(), "Error report generated at some/path")
    }

    @Suppress("MaxLineLength")
    @Test
    fun `Given a report object + an exception in the repository when generate report is called then error is logged`() {
        val logMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(logMessages, errorMessages)

        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(fakeLogger, fakePopcornGuineapigRepositoryWithError)

        generateArchitectureViolationReport.execute(
            reportPath = "",
            results = mapOf(
                "moduleName" to fakeCheckResult
            )
        )

        assertTrue(errorMessages.isNotEmpty())
        assertContains(errorMessages.first(), "Something went wrong to generate your error report.")
    }
}
