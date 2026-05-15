package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.data.report.toMarkDownFormat
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.ModuleMetric

internal class PopcornGuineapigRepositoryImpl(
    private val reportDataSource: ReportDataSource
) : PopcornGuineapigRepository {
    override fun exportErrorReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>
    ) {
        val reportContent = architectureViolationReportData.map { it.toMarkDownFormat() }
            .reduceOrNull { acc, value -> "$acc\n$value" }
            .orEmpty()

        reportDataSource.exportErrorReportInMarkdown(
            fullPath = reportPath,
            reportContent = reportContent
        )
    }

    override fun exportMetricsReport(
        reportPath: String,
        metrics: List<ModuleMetric>
    ) {
        val reportContent = metrics.map {
            "${it.name},${it.fanIn},${it.fanOut},${it.instability}"
        }.reduceOrNull { acc, item ->
            "$acc\n$item"
        }

        val header = "name,fanIn,fanOut,instability\n"

        reportDataSource.exportMetricsReportInCsv(
            fullPath = reportPath,
            reportContent = header.plus(reportContent)
        )
    }
}
