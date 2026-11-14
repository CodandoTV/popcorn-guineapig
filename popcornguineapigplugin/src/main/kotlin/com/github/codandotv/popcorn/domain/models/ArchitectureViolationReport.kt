package com.github.codandotv.popcorn.domain.models

internal data class ArchitectureViolationReport(
    val modulePath: String,
    val analysisTable: List<ViolationReportItem>,
)
