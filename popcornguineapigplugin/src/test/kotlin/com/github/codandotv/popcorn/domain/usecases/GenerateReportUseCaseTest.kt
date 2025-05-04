package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.report.ReportInfo
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepository
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithError
import org.junit.Test
import kotlin.test.assertFails

class GenerateReportUseCaseTest {

    private lateinit var generateReportUseCase: GenerateReportUseCase
    private val fakeTargetModule = TargetModule(
        "moduleName", internalDependencies = listOf(
            InternalDependenciesMetadata(group = null, moduleName = "dep1"),
            InternalDependenciesMetadata(group = null, moduleName = "dep2")
        )
    )
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
        generateReportUseCase = GenerateReportUseCaseImpl(fakePopcornGuineapigRepository)

        // act
        generateReportUseCase.execute(
            ReportInfo(
                targetModule = fakeTargetModule,
                checkResult = fakeCheckResult,
                skippedRules = emptyList(),
            )
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `Given a report object + an exception in the repository when generate report is called then checks if the exception is being triggered`() {
        // arrange
        generateReportUseCase = GenerateReportUseCaseImpl(fakePopcornGuineapigRepositoryWithError)

        // act, assert
        assertFails {
            generateReportUseCase.execute(
                ReportInfo(
                    targetModule = fakeTargetModule,
                    checkResult = fakeCheckResult,
                    skippedRules = emptyList(),
                )
            )
        }
    }
}
