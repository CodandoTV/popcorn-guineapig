package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.report.AnalysisTableItemData
import com.github.codandotv.popcorn.domain.report.AnalysisTableResultEnumData
import com.github.codandotv.popcorn.domain.report.ReportData
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.report.ReportInfo
import kotlin.reflect.KClass

internal interface GenerateReportUseCase {
    fun execute(
        reportPath: String,
        reportInfo: ReportInfo
    )
}

internal class GenerateReportUseCaseImpl(
    private val repository: PopcornGuineapigRepository,
) : GenerateReportUseCase {

    override fun execute(
        reportPath: String,
        reportInfo: ReportInfo,
    ) {
        if (reportInfo.checkResult is CheckResult.Failure) {
            val analysisTable = reportInfo.checkResult.toAnalysisTableList(
                skippedRules = reportInfo.skippedRules
            )

            repository.exportReport(
                reportPath = reportPath,
                report = ReportData(
                    moduleName = reportInfo.targetModule.moduleName,
                    analysisTable = analysisTable,
                )
            )
        }
    }
}

internal fun InternalDependenciesMetadata.toName(): String {
    return group?.plus(":")?.plus(moduleName) ?: moduleName
}

internal fun CheckResult.Failure.toAnalysisTableList(
    skippedRules: List<KClass<*>>?
): List<AnalysisTableItemData> {
    return errors.map { arcViolation ->
        AnalysisTableItemData(
            internalDependencyName = arcViolation.affectedRelationship?.toName().orEmpty(),
            ruleChecked = arcViolation.rule::class.simpleName.toString(),
            ruleDescription = arcViolation.message,
            result = if (skippedRules?.contains(arcViolation.rule::class) == true) {
                AnalysisTableResultEnumData.SKIPPED
            } else {
                AnalysisTableResultEnumData.FAILED
            }
        )
    }
}
