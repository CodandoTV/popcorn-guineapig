package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.data.report.ReportDataSource

interface PopcornGuineapigRepository {
    fun exportReport(report: ReportDto): String?
}

internal class PopcornGuineapigRepositoryImpl(
    private val reportPath: String,
    private val reportDataSource: ReportDataSource
) : PopcornGuineapigRepository {
    override fun exportReport(report: ReportDto): String? {
        return runCatching {
            reportDataSource.export(
                fullPath = reportPath,
                reportDto = report
            )
            reportPath
        }.getOrNull()
    }
}
