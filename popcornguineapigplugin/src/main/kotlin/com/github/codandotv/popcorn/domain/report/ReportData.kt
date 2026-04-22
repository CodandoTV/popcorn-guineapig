package com.github.codandotv.popcorn.domain.report

internal data class ReportData(
    val moduleName: String,
    val analysisTable: List<AnalysisTableItemData>,
)
