package com.github.codandotv.popcorn.domain.models

internal data class ArchitectureViolationReport(
    val moduleName: String,
    val analysisTable: List<ViolationReportItem>,
)
