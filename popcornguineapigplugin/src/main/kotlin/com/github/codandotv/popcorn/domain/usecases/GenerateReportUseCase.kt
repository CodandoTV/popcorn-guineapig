package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.report.AnalysisTableItemData
import com.github.codandotv.popcorn.domain.report.AnalysisTableResultEnumData
import com.github.codandotv.popcorn.domain.report.ReportData
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.CheckResult
import kotlin.collections.map

internal interface GenerateReportUseCase {
    fun execute(
        reportPath: String,
        results: Map<String, CheckResult>
    )
}

internal class GenerateReportUseCaseImpl(
    private val repository: PopcornGuineapigRepository,
) : GenerateReportUseCase {

    override fun execute(
        reportPath: String,
        results: Map<String, CheckResult>,
    ) {
        val failureResults = results.filter {
            it.value is CheckResult.Failure
        }

        val reportData = mutableListOf<ReportData>()

        failureResults.forEach { (moduleName, result) ->
            val analysisTable = mutableListOf<AnalysisTableItemData>()
            if (result is CheckResult.Failure) {
                val tableItems = result.errors.map { arcViolation ->
                    AnalysisTableItemData(
                        internalDependencyName = arcViolation.affectedRelationship?.toName()
                            .orEmpty(),
                        ruleChecked = arcViolation.rule::class.simpleName.toString(),
                        ruleDescription = arcViolation.message,
                        result = AnalysisTableResultEnumData.FAILED,
                    )
                }
                analysisTable.addAll(tableItems)
            }
            reportData.add(
                ReportData(
                    moduleName = moduleName,
                    analysisTable = analysisTable
                )
            )
        }

        repository.exportReport(
            reportPath = reportPath,
            reportData = reportData
        )
    }
}

internal fun InternalDependenciesMetadata.toName(): String {
    return group?.plus(":")?.plus(moduleName) ?: moduleName
}
