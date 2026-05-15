package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.CheckResult
import kotlin.collections.map

internal interface GenerateArchitectureViolationReport {
    fun execute(
        reportPath: String,
        results: Map<String, CheckResult>
    )
}

internal class GenerateArchitectureViolationReportImpl(
    private val repository: PopcornGuineapigRepository,
) : GenerateArchitectureViolationReport {

    override fun execute(
        reportPath: String,
        results: Map<String, CheckResult>,
    ) {
        val failureResults = results.filter {
            it.value is CheckResult.Failure
        }

        val architectureViolationReportData = mutableListOf<ArchitectureViolationReport>()

        failureResults.forEach { (moduleName, result) ->
            val analysisTable = mutableListOf<ViolationReportItem>()
            if (result is CheckResult.Failure) {
                val tableItems = result.errors.map { arcViolation ->
                    ViolationReportItem(
                        internalDependencyName = arcViolation.affectedRelationship?.toName()
                            .orEmpty(),
                        ruleChecked = arcViolation.rule::class.simpleName.toString(),
                        ruleDescription = arcViolation.message,
                        result = ViolationReportType.FAILED,
                    )
                }
                analysisTable.addAll(tableItems)
            }
            architectureViolationReportData.add(
                ArchitectureViolationReport(
                    moduleName = moduleName,
                    analysisTable = analysisTable
                )
            )
        }

        repository.exportErrorReport(
            reportPath = reportPath,
            architectureViolationReportData = architectureViolationReportData
        )
    }
}

internal fun InternalDependenciesMetadata.toName(): String {
    return group?.plus(":")?.plus(moduleName) ?: moduleName
}
