package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.data.PopcornGuineapigRepository
import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto
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
        val reportDto = reportInfo.toReportDTO()
        repository.exportReport(reportDto)
    }
}

internal fun ReportInfo.toReportDTO(): ReportDto {
    val howCanIFixThisItemDto = if (checkResult is CheckResult.Failure) {
        checkResult.errors.mapNotNull { error ->
            if (skippedRules?.contains(error.rule::class) == true) {
                null
            } else {
                HowCanIFixThisItemDto(
                    violatedRule = error.rule::class.simpleName.toString(),
                    message = error.message
                )
            }
        }
    } else emptyList()

    val internalDependenciesFormatted = targetModule.internalDependencies.map { it.moduleName }

    val notSkippedRulesFormatted = configuration.rules.filter { rule ->
        skippedRules?.contains(rule::class) == false
    }.map { rule -> rule::class.simpleName.toString() }

    val analysisTable = mutableListOf<AnalysisTableItemDto>()
    targetModule.internalDependencies.forEach { internalDep ->
        configuration.rules.forEach { rule ->
            analysisTable.add(
                AnalysisTableItemDto(
                    internalDependencyName = internalDep.moduleName,
                    ruleChecked = rule::class.simpleName.toString(),
                    result = if (skippedRules?.contains(rule::class) == true) {
                        AnalysisTableResultEnumDto.SKIPPED
                    } else if (checkResult.isRuleFailing(rule::class)) {
                        AnalysisTableResultEnumDto.FAILED
                    } else {
                        AnalysisTableResultEnumDto.PASSED
                    }
                )
            )
        }
    }

    val skippedRulesFormatted = skippedRules?.map { it.simpleName.toString() } ?: emptyList()

    return ReportDto(
        moduleName = targetModule.moduleName,
        analysisTable = analysisTable,
        howCanIFixThis = howCanIFixThisItemDto,
        internalDependenciesItems = internalDependenciesFormatted,
        notSkippedRules = notSkippedRulesFormatted,
        skippedRules = skippedRulesFormatted,
        dateTimestamp = dateTimestamp
    )
}


private infix fun CheckResult.isRuleFailing(rule: KClass<*>): Boolean {
    return this is CheckResult.Failure && errors.any { it.rule::class == rule }
}
