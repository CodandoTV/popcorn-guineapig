package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.Logger
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.models.ModuleAnalysis
import kotlin.collections.map

internal interface GenerateArchitectureViolationReport {
    fun execute(
        reportPath: String,
        results: List<ModuleAnalysis>
    )
}

internal class GenerateArchitectureViolationReportImpl(
    private val logger: Logger,
    private val repository: PopcornGuineapigRepository,
) : GenerateArchitectureViolationReport {

    override fun execute(
        reportPath: String,
        results: List<ModuleAnalysis>,
    ) {
        val failureResults = results.filter {
            it.result is CheckResult.Failure
        }

        val architectureViolationReportData = mutableListOf<ArchitectureViolationReport>()

        failureResults.forEach { analysis ->
            val failure = analysis.result as CheckResult.Failure
            val analysisTable = failure.errors.flatMap { arcViolation ->
                arcViolation.affectedRelationship.orEmpty().map { affectedRelationship ->
                    ViolationReportItem(
                        internalDependencyName = affectedRelationship.toName(),
                        ruleChecked = arcViolation.rule::class.simpleName.toString(),
                        ruleDescription = arcViolation.message,
                        result = ViolationReportType.FAILED,
                    )
                }
            }
            architectureViolationReportData.add(
                ArchitectureViolationReport(
                    modulePath = analysis.modulePath,
                    analysisTable = analysisTable
                )
            )
        }

        runCatching {
            repository.exportErrorReport(
                reportPath = reportPath,
                architectureViolationReportData = architectureViolationReportData
            )
        }.onSuccess {
            logger.logSuccess("Error report generated at $reportPath")
        }.onFailure {
            logger.logError("Something went wrong to generate your error report.")
        }
    }
}

internal fun InternalDependenciesMetadata.toName(): String {
    return group?.plus(":")?.plus(moduleName) ?: moduleName
}
