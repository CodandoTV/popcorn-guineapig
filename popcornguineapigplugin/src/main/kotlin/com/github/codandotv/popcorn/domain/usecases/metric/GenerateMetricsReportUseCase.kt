package com.github.codandotv.popcorn.domain.usecases.metric

import com.github.codandotv.popcorn.domain.Logger
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.TargetModule

internal interface GenerateMetricsReportUseCase {
    fun invoke(
        metricsReportPath: String,
        modules: List<TargetModule>
    )
}

internal class GenerateMetricsReportUseCaseImpl(
    private val collectModuleMetricsUseCase: CollectModuleMetricsUseCase,
    private val repository: PopcornGuineapigRepository,
    private val logger: Logger,
) : GenerateMetricsReportUseCase {
    override fun invoke(
        metricsReportPath: String,
        modules: List<TargetModule>
    ) {
        logger.log("Process popcorn metrics over all modules")

        val metrics = collectModuleMetricsUseCase.invoke(
            modules = modules
        )

        logger.log("Preparing report with ${metrics.size} metrics")

        runCatching {
            repository.exportMetricsReport(
                reportPath = metricsReportPath,
                metrics = metrics
            )
        }.onSuccess {
            logger.log("Check the report at $metricsReportPath...")
        }.onFailure {
            logger.logError("Something went wrong to generate the metrics report.")
        }
    }
}
