package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.data.report.PopcornGuineapigReportException
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.report.ReportData

internal val fakePopcornGuineapigRepository = object : PopcornGuineapigRepository {
    override fun exportReport(reportPath: String, reportData: List<ReportData>) = Unit
}

internal val fakePopcornGuineapigRepositoryWithError = object : PopcornGuineapigRepository {
    override fun exportReport(reportPath: String, reportData: List<ReportData>) {
        throw PopcornGuineapigReportException("/users/moro/documents/svn/myreport.md")
    }
}
