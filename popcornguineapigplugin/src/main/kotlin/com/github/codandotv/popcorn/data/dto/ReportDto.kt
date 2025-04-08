package com.github.codandotv.popcorn.data.dto

data class ReportDto(
    val moduleName: String,
    val analysisTable: List<AnalysisTableItemDto>,
)
