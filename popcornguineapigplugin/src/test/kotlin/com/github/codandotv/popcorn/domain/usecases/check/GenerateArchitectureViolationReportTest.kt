package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.ModuleAnalysis
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.fakes.FakeLogger
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepository
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithCallbacks
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithError
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
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
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = logMessages,
            successMessages = successMessages,
            errorMessages = errorMessages
        )

        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(fakeLogger, fakePopcornGuineapigRepository)

        generateArchitectureViolationReport.execute(
            reportPath = "some/path",
            results = listOf(
                ModuleAnalysis(
                    modulePath = ":moduleName",
                    result = fakeCheckResult,
                )
            )
        )

        assertContains(successMessages.first(), "Error report generated at some/path")
    }

    @Suppress("MaxLineLength")
    @Test
    fun `Given a report object + an exception in the repository when generate report is called then error is logged`() {
        val logMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = logMessages,
            successMessages = successMessages,
            errorMessages = errorMessages,
        )

        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(
                fakeLogger,
                fakePopcornGuineapigRepositoryWithError
            )

        generateArchitectureViolationReport.execute(
            reportPath = "",
            results = listOf(
                ModuleAnalysis(
                    modulePath = ":moduleName",
                    result = fakeCheckResult,
                )
            )
        )

        assertTrue(errorMessages.isNotEmpty())
        assertContains(errorMessages.first(), "Something went wrong to generate your error report.")
    }

    @Test
    fun `Given two failing modules sharing a name when report is generated then both are reported independently`() {
        val fakeLogger = FakeLogger(
            logMessages = mutableListOf(),
            successMessages = mutableListOf(),
            errorMessages = mutableListOf(),
        )
        var capturedReports: List<ArchitectureViolationReport> = emptyList()
        val fakeRepository = fakePopcornGuineapigRepositoryWithCallbacks(
            onErrorReportDataCallback = { reports -> capturedReports = reports }
        )
        val failure = CheckResult.Failure(
            errors = listOf(
                ArchitectureViolationError(
                    message = "This module should not have dependencies",
                    affectedRelationship = listOf(
                        InternalDependenciesMetadata(
                            group = "com.popcorn",
                            moduleName = "domain",
                            modulePath = ":car:domain",
                        )
                    ),
                    rule = NoDependencyRule(),
                )
            )
        )

        generateArchitectureViolationReport =
            GenerateArchitectureViolationReportImpl(fakeLogger, fakeRepository)

        generateArchitectureViolationReport.execute(
            reportPath = "some/path",
            results = listOf(
                ModuleAnalysis(modulePath = ":car:data", result = failure),
                ModuleAnalysis(modulePath = ":rent:data", result = failure),
            )
        )

        assertEquals(
            listOf(":car:data", ":rent:data"),
            capturedReports.map { it.modulePath }
        )
        assertTrue(capturedReports.all { it.analysisTable.size == 1 })
        assertEquals(
            "com.popcorn:domain",
            capturedReports.first().analysisTable.first().internalDependencyName
        )
    }
}
