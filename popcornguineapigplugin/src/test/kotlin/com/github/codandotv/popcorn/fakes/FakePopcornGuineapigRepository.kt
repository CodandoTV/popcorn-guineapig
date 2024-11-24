package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.data.PopcornGuineapigRepository
import com.github.codandotv.popcorn.data.dto.ReportDto
import com.github.codandotv.popcorn.data.report.PopcornGuineapigReportException

internal val fakePopcornGuineapigRepository = object : PopcornGuineapigRepository {
    override fun exportReport(report: ReportDto) = Unit
}

internal val fakePopcornGuineapigRepositoryWithError = object : PopcornGuineapigRepository {
    override fun exportReport(report: ReportDto) {
        throw PopcornGuineapigReportException("/users/moro/documents/svn/myreport.md")
    }
}