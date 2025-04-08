package com.github.codandotv.popcorn.data.dto

data class AnalysisTableItemDto(
    val internalDependencyName: String,
    val ruleChecked: String,
    val ruleDescription: String,
    val result: AnalysisTableResultEnumDto,
)
