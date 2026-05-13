package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.Logger
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
    private val logger: Logger,
) : AnalyseArchitectureUseCase {
    override fun execute(
        modules: List<TargetModule>,
        errorReportPath: String?,
    ) {
        val results = mutableMapOf<String, CheckResult>()

        modules.forEach {
            logger.log("Process popcorn task over ${it.moduleName}")
            logger.log(it.toString())

            val result = checkArchitectureUseCase.execute(
                targetModule = it,
            )
            results[it.moduleName] = result

            if (result is CheckResult.Failure) {
                result.errors.forEach { error ->
                    logger.logError(
                        "${it.moduleName} is violating the rule " +
                                error.rule::class.simpleName
                                + "(${error.message})"
                    )
                }
            }
        }

        errorReportPath?.let {
            generateReportUseCase.execute(
                reportPath = errorReportPath,
                results = results
            )
        }

        val shouldTriggerError = results.any { it.value is CheckResult.Failure }
        if(shouldTriggerError) {
            error("Something went wrong. Check the logs above for more details.")
        }
    }
}
