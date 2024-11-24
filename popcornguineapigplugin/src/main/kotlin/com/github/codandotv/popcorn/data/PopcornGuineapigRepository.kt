package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.data.report.ReportDataSource

interface PopcornGuineapigRepository {
    suspend fun exportReport(report: ReportDto)
}

internal class PopcornGuineapigRepositoryImpl(
    private val reportPath: String,
    private val reportDataSource: ReportDataSource
) : PopcornGuineapigRepository {
    override suspend fun exportReport(report: ReportDto) {
        reportDataSource.export(
            fullPath = reportPath,
            reportDto = report
        )
        reportPath
    }
}
