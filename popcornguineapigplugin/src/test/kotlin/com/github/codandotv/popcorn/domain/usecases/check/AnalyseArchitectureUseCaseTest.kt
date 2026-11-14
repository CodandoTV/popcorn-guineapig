@file:Suppress("MaxLineLength")

package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.models.ModuleAnalysis
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
            modulePath = ":data",
            moduleName = "data",
            internalDependencies = emptyList(),
            rules = listOf(fakeRule),
        )
    )
    private val aModuleWithFailure = TargetModule(
        modulePath = ":presentation",
        moduleName = "presentation",
        internalDependencies = listOf(
            InternalDependenciesMetadata(group = null, moduleName = "domain"),
        ),
        rules = listOf(fakeRule),
    )

    private fun fakeLogger(): FakeLogger {
        return FakeLogger(
            logMessages = mutableListOf(),
            errorMessages = mutableListOf(),
            successMessages = mutableListOf(),
        )
    }

    @Test
    fun `Given all modules passing when execute with no report path then no error is thrown and no log error is called`() {
        // Arrange
        val fakeLogger = fakeLogger()
        var reportCalled = false
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(":data" to CheckResult.Success)
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
        assertTrue { fakeLogger.logMessages.isNotEmpty() }
        assertTrue { fakeLogger.errorMessages.isEmpty() }
        assertTrue { fakeLogger.successMessages.isNotEmpty() }
        assertFalse(reportCalled)
    }

    @Test
    fun `Given some modules failing when execute with no report path then error is thrown and log error is called`() {
        // Arrange
        val fakeLogger = fakeLogger()
        var reportCalled = false
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    ":data" to CheckResult.Success,
                    ":presentation" to CheckResult.Failure(listOf(fakeError)),
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
        assertTrue(fakeLogger.errorMessages.isNotEmpty())
        assertFalse(reportCalled)
    }

    @Test
    fun `Given all modules passing with a report path when execute then report is generated`() {
        // Arrange
        val fakeLogger = fakeLogger()
        var capturedReportPath: String? = null
        var capturedResults: List<ModuleAnalysis>? = null
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(":data" to CheckResult.Success)
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
        assertTrue(fakeLogger.errorMessages.isEmpty())
    }

    @Test
    fun `Given some modules failing with a report path when execute then both error is thrown and report is generated`() {
        // Arrange
        val fakeLogger = fakeLogger()
        var reportCalled = false
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    ":presentation" to CheckResult.Failure(listOf(fakeError)),
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
        assertTrue(fakeLogger.errorMessages.isNotEmpty())
        assertTrue(fakeLogger.logMessages.isNotEmpty())
        assertTrue(reportCalled)
    }

    @Test
    fun `Given multiple modules all passing when execute then no error is thrown`() {
        // Arrange
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    ":data" to CheckResult.Success,
                    ":domain" to CheckResult.Success,
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> }
            ),
            logger = fakeLogger(),
        )

        // Act
        analyseArchitectureUseCase.execute(
            modules = listOf(
                TargetModule(
                    modulePath = ":data",
                    moduleName = "data",
                    internalDependencies = emptyList(),
                    rules = listOf(fakeRule),
                ),
                TargetModule(
                    modulePath = ":domain",
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
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    ":data" to CheckResult.Success,
                    ":presentation" to CheckResult.Failure(listOf(fakeError)),
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> }
            ),
            logger = fakeLogger(),
        )

        // Act, Assert
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(
                    TargetModule(
                        modulePath = ":data",
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
        val fakeLogger = fakeLogger()
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
        assertTrue(fakeLogger.logMessages.isEmpty())
        assertTrue(fakeLogger.errorMessages.isEmpty())
    }

    @Test
    fun `Given two modules sharing a name where one fails when execute then error is thrown for both iteration orders`() {
        // Arrange
        val carData = TargetModule(
            modulePath = ":car:data",
            moduleName = "data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
            ),
            rules = listOf(fakeRule),
        )
        val rentData = TargetModule(
            modulePath = ":rent:data",
            moduleName = "data",
            internalDependencies = emptyList(),
            rules = listOf(fakeRule),
        )
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    ":car:data" to CheckResult.Failure(listOf(fakeError)),
                    ":rent:data" to CheckResult.Success,
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, _ -> }
            ),
            logger = fakeLogger(),
        )

        // Act, Assert
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(carData, rentData),
                errorReportPath = null,
            )
        }
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(rentData, carData),
                errorReportPath = null,
            )
        }
    }

    @Test
    fun `Given two modules sharing a name when execute with a report path then both modules are reported independently`() {
        // Arrange
        val carData = TargetModule(
            modulePath = ":car:data",
            moduleName = "data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
            ),
            rules = listOf(fakeRule),
        )
        val rentData = TargetModule(
            modulePath = ":rent:data",
            moduleName = "data",
            internalDependencies = emptyList(),
            rules = listOf(fakeRule),
        )
        var capturedResults: List<ModuleAnalysis>? = null
        val analyseArchitectureUseCase = AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = FakeCheckArchitectureUseCase(
                modulesResults = mapOf(
                    ":car:data" to CheckResult.Failure(listOf(fakeError)),
                    ":rent:data" to CheckResult.Success,
                )
            ),
            generateArchitectureViolationReport = FakeGenerateArchitectureViolationReport(
                onExecute = { _, results -> capturedResults = results }
            ),
            logger = fakeLogger(),
        )

        // Act, Assert
        assertFails {
            analyseArchitectureUseCase.execute(
                modules = listOf(rentData, carData),
                errorReportPath = "/tmp/report.md",
            )
        }
        assertNotNull(capturedResults)
        val results = capturedResults.orEmpty()
        assertEquals(2, results.size)
        assertEquals(setOf(":car:data", ":rent:data"), results.map { it.modulePath }.toSet())
        assertTrue(results.any { it.modulePath == ":car:data" && it.result is CheckResult.Failure })
    }
}
