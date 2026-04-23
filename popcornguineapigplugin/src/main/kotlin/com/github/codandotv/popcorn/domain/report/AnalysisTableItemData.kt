package com.github.codandotv.popcorn.domain.report

internal data class AnalysisTableItemData(
    val internalDependencyName: String,
    val ruleChecked: String,
    val ruleDescription: String,
    val result: AnalysisTableResultEnumData,
)
