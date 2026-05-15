package com.github.codandotv.popcorn.domain

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport

internal interface PopcornGuineapigRepository {
    fun exportReport(reportPath: String, architectureViolationReportData: List<ArchitectureViolationReport>)
}
