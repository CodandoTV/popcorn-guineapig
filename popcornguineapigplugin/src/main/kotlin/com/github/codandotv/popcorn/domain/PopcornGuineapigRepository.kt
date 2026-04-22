package com.github.codandotv.popcorn.domain

import com.github.codandotv.popcorn.domain.report.ReportData

internal interface PopcornGuineapigRepository {
    fun exportReport(reportPath: String, report: ReportData)
}