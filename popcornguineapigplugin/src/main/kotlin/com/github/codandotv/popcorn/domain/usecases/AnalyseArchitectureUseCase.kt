package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.CheckResult

internal interface AnalyseArchitectureUseCase {
    fun execute(
        modules: List<TargetModule>,
        errorReportPath: String?,
    )
}

internal class AnalyseArchitectureUseCaseImpl(
    private val checkArchitectureUseCase: CheckArchitectureUseCase,
    private val generateReportUseCase: GenerateReportUseCase,
) : AnalyseArchitectureUseCase {
    override fun execute(
        modules: List<TargetModule>,
        errorReportPath: String?,
    ) {
        val results = mutableMapOf<String, CheckResult>()

        modules.forEach {
            val result = checkArchitectureUseCase.execute(
                targetModule = it,
            )
            results[it.moduleName] = result
        }

        errorReportPath?.let { errorReportPath ->
            generateReportUseCase.execute(
                reportPath = errorReportPath,
                results = results
            )
        }

        // trigger errors
    }
}
