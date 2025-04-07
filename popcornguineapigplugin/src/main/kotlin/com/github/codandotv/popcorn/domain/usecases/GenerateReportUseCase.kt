package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.data.PopcornGuineapigRepository
import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.report.ReportInfo
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule
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
            val howCanIFixThisItemDto = reportInfo.checkResult.errors.toHowCanIFixThisItemDTO(
                reportInfo.skippedRules
            )
            val (notSkippedRules, skippedRules) = reportInfo.configuration.rules.toNotSkippedAndSkipped(
                skippedRules = reportInfo.skippedRules
            )

            val analysisTable = reportInfo.checkResult.toAnalysisTableList(
                skippedRules = reportInfo.skippedRules
            )

            repository.exportReport(
                ReportDto(
                    moduleName = reportInfo.targetModule.moduleName,
                    howCanIFixThis = howCanIFixThisItemDto,
                    analysisTable = analysisTable,
                    internalDependenciesItems = reportInfo
                        .targetModule
                        .internalDependencies
                        .toInternalDependenciesNameList(),
                    notSkippedRules = notSkippedRules,
                    skippedRules = skippedRules,
                )
            )
        }
    }
}

internal fun List<ArchitectureViolationError>.toHowCanIFixThisItemDTO(skippedRules: List<KClass<*>>?) =
    mapNotNull { error ->
        if (skippedRules?.contains(error.rule::class) == true) {
            null
        } else {
            HowCanIFixThisItemDto(
                violatedRule = error.rule::class.simpleName.toString(),
                message = error.message
            )
        }
    }

internal fun InternalDependenciesMetadata.toName(): String {
    return group?.plus(":")?.plus(moduleName) ?: moduleName
}

internal fun List<InternalDependenciesMetadata>.toInternalDependenciesNameList() = map {
    it.toName()
}

internal fun List<PopcornGuineaPigRule>.toNotSkippedAndSkipped(
    skippedRules: List<KClass<*>>?
): Pair<List<String>, List<String>> {
    val (notSkipped, skipped) = partition { rule ->
        skippedRules?.contains(rule::class) == false
    }
    return Pair(
        notSkipped.mapNotNull { it::class.simpleName },
        skipped.mapNotNull { it::class.simpleName }
    )
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
