package com.github.codandotv.popcorn.data.dto

data class ReportDto(
    val moduleName: String,
    val internalDependenciesItems: List<String>,
    val skippedRules: List<String>,
    val notSkippedRules: List<String>,
    val analysisTable: List<AnalysisTableItemDto>,
    val howCanIFixThis: List<HowCanIFixThisItemDto>
)
