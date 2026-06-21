package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.data.report.toMarkdownReport
import com.github.codandotv.popcorn.data.report.toMetricsReport
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.ModuleMetric

internal class PopcornGuineapigRepositoryImpl(
    private val reportDataSource: ReportDataSource,
    private val skillDataSource: SkillDataSource,
) : PopcornGuineapigRepository {
    override fun exportErrorReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>
    ) {
        val reportContent = architectureViolationReportData.toMarkdownReport()
        reportDataSource.exportErrorReportInMarkdown(
            fullPath = reportPath,
            reportContent = reportContent
        )
    }

    override fun exportMetricsReport(
        reportPath: String,
        metrics: List<ModuleMetric>
    ) {
        reportDataSource.exportMetricsReportInCsv(
            fullPath = reportPath,
            reportContent = metrics.toMetricsReport()
        )
    }

    override fun installSkill(
        skillOutputDir: String,
        skillName: String,
    ) {
        skillDataSource.installSkill(
            skillOutputDir = skillOutputDir,
            skillName = skillName,
        ).getOrThrow()
    }
}
