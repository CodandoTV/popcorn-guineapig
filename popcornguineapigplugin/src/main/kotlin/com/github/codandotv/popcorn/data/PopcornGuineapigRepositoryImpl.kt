package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.domain.report.ReportData
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository

internal class PopcornGuineapigRepositoryImpl(
    private val reportDataSource: ReportDataSource
) : PopcornGuineapigRepository {
    override fun exportReport(reportPath: String, reportData: List<ReportData>) {
        reportDataSource.export(
            fullPath = reportPath,
            reportData = reportData
        )
    }
}
