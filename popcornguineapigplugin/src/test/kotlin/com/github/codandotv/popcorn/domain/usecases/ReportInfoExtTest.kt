package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.domain.input.PopcornProject
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.report.ReportInfo
import com.github.codandotv.popcorn.domain.rules.JustWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportInfoExtTest {

    private val fakeReportInfo = ReportInfo(
        targetModule = TargetModule(
            moduleName = "chuck norris",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, "bruce lee"),
                InternalDependenciesMetadata(group = null, "van diesel")
            ),
        ),
        checkResult = CheckResult.Success,
        configuration = PopcornConfiguration(
            project = PopcornProject(
                type = ProjectType.KMP,
                groupName = null
            ),
            rules = listOf(JustWithRule(listOf("bruce lee", "van diesel")))
        ),
        skippedRules = emptyList(),
        dateTimestamp = "dateTimestamp"
    )

    @Test
    fun `Given an execution when toReportDTO is called then check if the errors are well described`() {
        // arrange
        val expected = ReportDto(
            moduleName = "chuck norris",
            internalDependenciesItems = listOf("bruce lee", "van diesel"),
            howCanIFixThis = emptyList(),
            notSkippedRules = listOf("JustWithRule"),
            skippedRules = emptyList(),
            dateTimestamp = "dateTimestamp",
            analysisTable = listOf(
                AnalysisTableItemDto(
                    internalDependencyName = "bruce lee",
                    ruleChecked = "JustWithRule",
                    result = AnalysisTableResultEnumDto.PASSED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "van diesel",
                    ruleChecked = "JustWithRule",
                    result = AnalysisTableResultEnumDto.PASSED
                )
            )
        )
        val input = fakeReportInfo

        // act
        val result = input.toReportDTO()

        // assert
        assertEquals(
            expected,
            result
        )
    }

    @Suppress("LongMethod")
    @Test
    fun `Given an execution with some errors when toReportDTO is called then check if the errors are well described`() {
        // arrange
        val expected = ReportDto(
            moduleName = "chuck norris",
            internalDependenciesItems = listOf("bruce lee", "van diesel"),
            howCanIFixThis = listOf(
                HowCanIFixThisItemDto(
                    violatedRule = "NoDependencyRule",
                    message = "This module should not have dependencies"
                )
            ),
            notSkippedRules = listOf(
                "JustWithRule",
                "NoDependencyRule"
            ),
            skippedRules = emptyList(),
            dateTimestamp = "dateTimestamp",
            analysisTable = listOf(
                AnalysisTableItemDto(
                    internalDependencyName = "bruce lee",
                    ruleChecked = "JustWithRule",
                    result = AnalysisTableResultEnumDto.PASSED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "bruce lee",
                    ruleChecked = "NoDependencyRule",
                    result = AnalysisTableResultEnumDto.FAILED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "van diesel",
                    ruleChecked = "JustWithRule",
                    result = AnalysisTableResultEnumDto.PASSED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "van diesel",
                    ruleChecked = "NoDependencyRule",
                    result = AnalysisTableResultEnumDto.FAILED
                )
            )
        )
        val input = fakeReportInfo.copy(
            configuration = fakeReportInfo.configuration.copy(
                rules = fakeReportInfo.configuration.rules.toMutableList().apply {
                    add(NoDependencyRule())
                },
            ),
            checkResult = CheckResult.Failure(
                errors = listOf(
                    ArchitectureViolationError(
                        "This module should not have dependencies",
                        NoDependencyRule()
                    )
                )
            )
        )

        // act
        val result = input.toReportDTO()

        // assert
        assertEquals(
            expected,
            result
        )
    }

    @Suppress("MaxLineLength")
    @Test
    fun `Given an execution with some skipped rules when toReportDTO is called then check if the errors are well described`() {
        // arrange
        val expected = ReportDto(
            moduleName = "chuck norris",
            internalDependenciesItems = listOf("bruce lee", "van diesel"),
            howCanIFixThis = emptyList(),
            notSkippedRules = listOf(
                "JustWithRule",
            ),
            skippedRules = listOf(
                "NoDependencyRule"
            ),
            dateTimestamp = "dateTimestamp",
            analysisTable = listOf(
                AnalysisTableItemDto(
                    internalDependencyName = "bruce lee",
                    ruleChecked = "JustWithRule",
                    result = AnalysisTableResultEnumDto.PASSED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "bruce lee",
                    ruleChecked = "NoDependencyRule",
                    result = AnalysisTableResultEnumDto.SKIPPED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "van diesel",
                    ruleChecked = "JustWithRule",
                    result = AnalysisTableResultEnumDto.PASSED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "van diesel",
                    ruleChecked = "NoDependencyRule",
                    result = AnalysisTableResultEnumDto.SKIPPED
                )
            )
        )
        val input = fakeReportInfo.copy(
            configuration = fakeReportInfo.configuration.copy(
                rules = fakeReportInfo.configuration.rules.toMutableList().apply {
                    add(NoDependencyRule())
                },
            ),
            skippedRules = listOf(NoDependencyRule::class),
            checkResult = CheckResult.Failure(
                errors = listOf(
                    ArchitectureViolationError(
                        "This module should not have dependencies",
                        NoDependencyRule()
                    )
                )
            )
        )

        // act
        val result = input.toReportDTO()

        // assert
        assertEquals(
            expected,
            result
        )
    }
}
