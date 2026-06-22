@file:Suppress("MaxLineLength")

package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.fakes.FakeCheckArchitectureUseCase
import com.github.codandotv.popcorn.fakes.FakeGenerateArchitectureViolationReport
import com.github.codandotv.popcorn.fakes.FakeLogger
import junit.framework.TestCase.assertFalse
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AnalyseArchitectureUseCaseTest {

    private val fakeRule = NoDependencyRule()
    private val fakeError = ArchitectureViolationError(
        message = "no dependency rule violated",
        affectedRelationship = null,
        rule = fakeRule,
    )
    private val someModules = listOf(
        TargetModule(
            moduleName = "data",
            internalDependencies = emptyList(),
            rules = listOf(fakeRule),
        )
    )
    private val aModuleWithFailure = TargetModule(
        moduleName = "presentation",
        internalDependencies = listOf(
            InternalDependenciesMetadata(group = null, moduleName = "domain"),
        ),
        rules = listOf(fakeRule),
    )

    @Test
    fun `Given all modules passing when execute with no report path then no error is thrown and no log error is called`() {
        // Arrange
        val loggerMessages = mutableListOf<String>()
        val loggerErrorMessages = mutableListOf<String>()
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = loggerMessages,
            errorMessages = loggerErrorMessages,
            successMessages = successMessages,
        )
        var reportCalled = false
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf("data" to CheckResult.Success)
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> reportCalled = true }
            ),
            logger = fakeLogger,
        )

        // Act
        analyseArchitectureUseCase.execute(
            modules = someModules,
            errorReportPath = null,
        )

        // Assert
        assertTrue { loggerMessages.isNotEmpty() }
        assertTrue { loggerErrorMessages.isEmpty() }
        assertTrue { successMessages.isNotEmpty() }
        assertFalse(reportCalled)
    }

    @Test
    fun `Given some modules failing when execute with no report path then error is thrown and log error is called`() {
        // Arrange
        val loggerMessages = mutableListOf<String>()
        val loggerErrorMessages = mutableListOf<String>()
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = loggerMessages,
            errorMessages = loggerErrorMessages,
            successMessages = successMessages,
        )
        var reportCalled = false
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    "data" to CheckResult.Success,
                    "presentation" to CheckResult.Failure(listOf(fakeError)),
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> reportCalled = true }
            ),
            logger = fakeLogger,
        )

        // Act, Assert
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(aModuleWithFailure),
                errorReportPath = null,
            )
        }
        assertTrue(loggerErrorMessages.isNotEmpty())
        assertFalse(reportCalled)
    }

    @Test
    fun `Given all modules passing with a report path when execute then report is generated`() {
        // Arrange
        val loggerMessages = mutableListOf<String>()
        val loggerErrorMessages = mutableListOf<String>()
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = loggerMessages,
            errorMessages = loggerErrorMessages,
            successMessages = successMessages,
        )
        var capturedReportPath: String? = null
        var capturedResults: Map<String, CheckResult>? = null
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf("data" to CheckResult.Success)
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { path, results ->
                    capturedReportPath = path
                    capturedResults = results
                }
            ),
            logger = fakeLogger,
        )

        // Act
        analyseArchitectureUseCase.execute(
            modules = someModules,
            errorReportPath = "/tmp/report.md",
        )

        // Assert
        assertNotNull(capturedReportPath)
        assertNotNull(capturedResults)
        assertEquals("/tmp/report.md", capturedReportPath)
        assertTrue(loggerErrorMessages.isEmpty())
    }

    @Test
    fun `Given some modules failing with a report path when execute then both error is thrown and report is generated`() {
        // Arrange
        val loggerMessages = mutableListOf<String>()
        val loggerErrorMessages = mutableListOf<String>()
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = loggerMessages,
            errorMessages = loggerErrorMessages,
            successMessages = successMessages,
        )
        var reportCalled = false
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    "presentation" to CheckResult.Failure(listOf(fakeError)),
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> reportCalled = true }
            ),
            logger = fakeLogger,
        )

        // Act, Assert
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(aModuleWithFailure),
                errorReportPath = "/tmp/report.md",
            )
        }
        assertTrue(loggerErrorMessages.isNotEmpty())
        assertTrue(loggerMessages.isNotEmpty())
        assertTrue(reportCalled)
    }

    @Test
    fun `Given multiple modules all passing when execute then no error is thrown`() {
        // Arrange
        val fakeLogger = FakeLogger(
            mutableListOf(),
            mutableListOf(),
            mutableListOf()
        )
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    "data" to CheckResult.Success,
                    "domain" to CheckResult.Success,
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> }
            ),
            logger = fakeLogger,
        )

        // Act
        analyseArchitectureUseCase.execute(
            modules = listOf(
                TargetModule(
                    moduleName = "data",
                    internalDependencies = emptyList(),
                    rules = listOf(fakeRule),
                ),
                TargetModule(
                    moduleName = "domain",
                    internalDependencies = emptyList(),
                    rules = listOf(fakeRule),
                ),
            ),
            errorReportPath = null,
        )
    }

    @Test
    fun `Given multiple modules with some passing and some failing when execute then error is thrown`() {
        // Arrange
        val fakeLogger = FakeLogger(
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
        )
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    "data" to CheckResult.Success,
                    "presentation" to CheckResult.Failure(listOf(fakeError)),
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> }
            ),
            logger = fakeLogger,
        )

        // Act, Assert
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(
                    TargetModule(
                        moduleName = "data",
                        internalDependencies = emptyList(),
                        rules = listOf(fakeRule),
                    ),
                    aModuleWithFailure,
                ),
                errorReportPath = null,
            )
        }
    }

    @Test
    fun `Given an empty list of modules when execute then no error is thrown and no log messages are produced`() {
        // Arrange
        val loggerMessages = mutableListOf<String>()
        val loggerErrorMessages = mutableListOf<String>()
        val successMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(
            logMessages = loggerMessages,
            errorMessages = loggerErrorMessages,
            successMessages = successMessages,
        )
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(modulesResults = emptyMap()),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> }
            ),
            logger = fakeLogger,
        )

        // Act
        analyseArchitectureUseCase.execute(
            modules = emptyList(),
            errorReportPath = null,
        )

        // Assert
        assertTrue(loggerMessages.isEmpty())
        assertTrue(loggerErrorMessages.isEmpty())
    }
}
