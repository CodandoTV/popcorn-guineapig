package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase

internal class FakeGenerateReportUseCase(
    internal val onExecute: (String, Map<String, CheckResult>) -> Unit,
) : GenerateReportUseCase {
    override fun execute(
        reportPath: String,
        results: Map<String, CheckResult>,
    ) {
        onExecute(reportPath, results)
    }
}
