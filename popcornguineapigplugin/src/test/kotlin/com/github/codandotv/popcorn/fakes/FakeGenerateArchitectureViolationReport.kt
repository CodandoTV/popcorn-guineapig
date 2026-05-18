package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.usecases.check.GenerateArchitectureViolationReport

internal class FakeGenerateArchitectureViolationReport(
    internal val onExecute: (String, Map<String, CheckResult>) -> Unit,
) : GenerateArchitectureViolationReport {
    override fun execute(
        reportPath: String,
        results: Map<String, CheckResult>,
    ) {
        onExecute(reportPath, results)
    }
}
