package com.github.codandotv.popcorn.domain.models

internal data class ViolationReportItem(
    val internalDependencyName: String,
    val ruleChecked: String,
    val ruleDescription: String,
    val result: ViolationReportType,
)
