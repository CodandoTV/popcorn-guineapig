package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportInfoExtTest {

    @Test
    fun `test howCanIFixThisItemDto when skipped rule exists`() {
        val errors = listOf(
            ArchitectureViolationError(
                message = "error",
                affectedRelationship = InternalDependenciesMetadata(
                    group = null,
                    moduleName = "chuckNorris"
                ),
                rule = DoNotWithRule(
                    notWith = listOf("chuckNorris")
                )
            )
        )

        val result = errors.toHowCanIFixThisItemDTO(skippedRules = listOf(DoNotWithRule::class))

        assertEquals(
            emptyList<HowCanIFixThisItemDto>(),
            result
        )
    }

    @Test
    fun `test howCanIFixThisItemDto when there is no skipped rule exists`() {
        val errors = listOf(
            ArchitectureViolationError(
                message = "error",
                affectedRelationship = InternalDependenciesMetadata(
                    group = null,
                    moduleName = "chuckNorris"
                ),
                rule = DoNotWithRule(
                    notWith = listOf("chuckNorris")
                )
            )
        )

        val result = errors.toHowCanIFixThisItemDTO(skippedRules = emptyList())
            .firstOrNull()
            ?.violatedRule

        assertEquals(
            "DoNotWithRule",
            result
        )
    }

    @Test
    fun `test toNotSkippedAndSkipped when there are skipped rules and errors`() {
        val result = listOf<PopcornGuineaPigRule>(
            DoNotWithRule(notWith = listOf("chuckNorris", "bruceLee")),
            NoDependencyRule()
        ).toNotSkippedAndSkipped(
            skippedRules = listOf(NoDependencyRule::class)
        )

        assertEquals(listOf("DoNotWithRule"), result.first)
        assertEquals(listOf("NoDependencyRule"), result.second)
    }

    @Test
    fun `test toNotSkippedAndSkipped when there are skipped rules`() {
        val result = listOf<PopcornGuineaPigRule>(
            NoDependencyRule()
        ).toNotSkippedAndSkipped(
            skippedRules = listOf(NoDependencyRule::class)
        )

        assertEquals(emptyList(), result.first)
        assertEquals(listOf("NoDependencyRule"), result.second)
    }

    @Test
    fun `test toNotSkippedAndSkipped when there are errors`() {
        val result = listOf<PopcornGuineaPigRule>(
            NoDependencyRule(),
            DoNotWithRule(notWith = listOf("x", "y"))
        ).toNotSkippedAndSkipped(
            skippedRules = emptyList()
        )

        assertEquals(listOf("NoDependencyRule", "DoNotWithRule"), result.first)
        assertEquals(emptyList(), result.second)
    }

    @Test
    fun `test toAnalysisTableList when there is NoDependencyRule violation`() {
        val result = CheckResult.Failure(
            errors = listOf(
                ArchitectureViolationError(
                    message = "",
                    rule = NoDependencyRule(),
                    affectedRelationship = InternalDependenciesMetadata(
                        group = null,
                        moduleName = "chuckNorris"
                    )
                )
            )
        ).toAnalysisTableList(
            skippedRules = emptyList()
        )

        assertEquals(
            listOf(
                AnalysisTableItemDto(
                    internalDependencyName = "chuckNorris",
                    ruleChecked = "NoDependencyRule",
                    result = AnalysisTableResultEnumDto.FAILED
                )
            ),
            result
        )
    }

    @Test
    fun `test toAnalysisTableList when there is a NoDependencyRule and a DoNotWith rule skipped`() {
        val result = CheckResult.Failure(
            errors = listOf(
                ArchitectureViolationError(
                    message = "",
                    rule = NoDependencyRule(),
                    affectedRelationship = InternalDependenciesMetadata(
                        group = null,
                        moduleName = "chuckNorris"
                    )
                ),
                ArchitectureViolationError(
                    message = "",
                    rule = DoNotWithRule(listOf("bruceLee")),
                    affectedRelationship = InternalDependenciesMetadata(
                        group = null,
                        moduleName = "bruceLee"
                    )
                )
            )
        ).toAnalysisTableList(
            skippedRules = listOf(DoNotWithRule::class)
        )

        assertEquals(
            listOf(
                AnalysisTableItemDto(
                    internalDependencyName = "chuckNorris",
                    ruleChecked = "NoDependencyRule",
                    result = AnalysisTableResultEnumDto.FAILED
                ),
                AnalysisTableItemDto(
                    internalDependencyName = "bruceLee",
                    ruleChecked = "DoNotWithRule",
                    result = AnalysisTableResultEnumDto.SKIPPED
                )
            ),
            result
        )
    }
}
