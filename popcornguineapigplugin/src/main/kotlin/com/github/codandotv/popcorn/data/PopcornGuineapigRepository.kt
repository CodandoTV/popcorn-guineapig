package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.data.report.ReportDataSource
import java.io.File

interface PopcornGuineapigRepository {
    fun exportReport(report: ReportDto, reportFile: File)
}

internal class PopcornGuineapigRepositoryImpl(
    private val reportDataSource: ReportDataSource
) : PopcornGuineapigRepository {
    override fun exportReport(report: ReportDto, reportFile: File) {
        reportDataSource.export(
            reportDto = report,
            reportFile = reportFile
        )
    }
}
