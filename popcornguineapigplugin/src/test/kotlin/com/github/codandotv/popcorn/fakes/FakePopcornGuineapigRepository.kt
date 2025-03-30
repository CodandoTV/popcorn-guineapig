package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.data.PopcornGuineapigRepository
import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.data.report.PopcornGuineapigReportException
import java.io.File

internal val fakePopcornGuineapigRepository = object : PopcornGuineapigRepository {
    override fun exportReport(report: ReportDto, reportFile: File) = Unit
}

internal val fakePopcornGuineapigRepositoryWithError = object : PopcornGuineapigRepository {
    override fun exportReport(report: ReportDto, reportFile: File) {
        throw PopcornGuineapigReportException("/users/moro/documents/svn/myreport.md")
    }
}
