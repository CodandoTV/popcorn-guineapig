package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.data.report.PopcornGuineapigReportException
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.report.ReportData

internal val fakePopcornGuineapigRepository = object : PopcornGuineapigRepository {
    override fun exportReport(reportPath: String, report: ReportData) = Unit
}

internal val fakePopcornGuineapigRepositoryWithError = object : PopcornGuineapigRepository {
    override fun exportReport(reportPath: String, report: ReportData) {
        throw PopcornGuineapigReportException("/users/moro/documents/svn/myreport.md")
    }
}
