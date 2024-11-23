package com.github.codandotv.popcorn.data.dto

data class ReportDto(
    val title: String,
    val internalDependenciesItems: List<String>,
    val skippedRules: List<String>,
    val notSkippedRules: List<String>,
    val analysisTable: List<AnalysisTableItemDto>
)