package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.data.PopcornGuineapigRepository
import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.report.ReportInfo
import kotlin.reflect.KClass

interface GenerateReportUseCase {
    fun execute(
        reportInfo: ReportInfo
    )
}

internal class GenerateReportUseCaseImpl(
    private val repository: PopcornGuineapigRepository,
) : GenerateReportUseCase {

    override fun execute(
        reportInfo: ReportInfo,
    ) {
        if (reportInfo.checkResult is CheckResult.Failure) {
            val analysisTable = reportInfo.checkResult.toAnalysisTableList(
                skippedRules = reportInfo.skippedRules
            )

            repository.exportReport(
                ReportDto(
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
): List<AnalysisTableItemDto> {
    return errors.map { arcViolation ->
        AnalysisTableItemDto(
            internalDependencyName = arcViolation.affectedRelationship?.toName().orEmpty(),
            ruleChecked = arcViolation.rule::class.simpleName.toString(),
            result = if (skippedRules?.contains(arcViolation.rule::class) == true) {
                AnalysisTableResultEnumDto.SKIPPED
            } else {
                AnalysisTableResultEnumDto.FAILED
            }
        )
    }
}
