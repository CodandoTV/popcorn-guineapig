package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.report.AnalysisTableItemData
import com.github.codandotv.popcorn.domain.report.AnalysisTableResultEnumData
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import kotlin.test.Test
import kotlin.test.assertEquals

class ReportInfoExtTest {

    @Test
    fun `test toAnalysisTableList when there is NoDependencyRule violation`() {
        val result = CheckResult.Failure(
            errors = listOf(
                ArchitectureViolationError(
                    message = "This module should not have dependencies",
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
                AnalysisTableItemData(
                    internalDependencyName = "chuckNorris",
                    ruleChecked = "NoDependencyRule",
                    ruleDescription = "This module should not have dependencies",
                    result = AnalysisTableResultEnumData.FAILED
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
                    message = "This module should not have dependencies",
                    rule = NoDependencyRule(),
                    affectedRelationship = InternalDependenciesMetadata(
                        group = null,
                        moduleName = "chuckNorris"
                    )
                ),
                ArchitectureViolationError(
                    message = "This module should not depends on [[bruceLee]]",
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
                AnalysisTableItemData(
                    internalDependencyName = "chuckNorris",
                    ruleChecked = "NoDependencyRule",
                    ruleDescription = "This module should not have dependencies",
                    result = AnalysisTableResultEnumData.FAILED
                ),
                AnalysisTableItemData(
                    internalDependencyName = "bruceLee",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "This module should not depends on [[bruceLee]]",
                    result = AnalysisTableResultEnumData.SKIPPED
                )
            ),
            result
        )
    }
}
