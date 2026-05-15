package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.data.report.toMarkDownFormat
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository

internal class PopcornGuineapigRepositoryImpl(
    private val reportDataSource: ReportDataSource
) : PopcornGuineapigRepository {
    override fun exportReport(reportPath: String, architectureViolationReportData: List<ArchitectureViolationReport>) {
        val reportContent = architectureViolationReportData.map { it.toMarkDownFormat() }
            .reduceOrNull { acc, value -> "$acc\n$value" }
            .orEmpty()

        reportDataSource.exportErrorReportInMarkdown(
            fullPath = reportPath,
            reportContent = reportContent
        )
    }
}
