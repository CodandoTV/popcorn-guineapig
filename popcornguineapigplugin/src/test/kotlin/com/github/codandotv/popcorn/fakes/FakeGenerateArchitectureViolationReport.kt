package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.models.ModuleAnalysis
import com.github.codandotv.popcorn.domain.usecases.check.GenerateArchitectureViolationReport

internal class FakeGenerateArchitectureViolationReport(
    internal val onExecute: (String, List<ModuleAnalysis>) -> Unit,
) : GenerateArchitectureViolationReport {
    override fun execute(
        reportPath: String,
        results: List<ModuleAnalysis>,
    ) {
        onExecute(reportPath, results)
    }
}
