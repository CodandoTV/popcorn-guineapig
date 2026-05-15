package com.github.codandotv.popcorn.domain

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.ModuleMetric

internal interface PopcornGuineapigRepository {
    fun exportErrorReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>
    )

    fun exportMetricsReport(
        reportPath: String,
        metrics: List<ModuleMetric>
    )
}
