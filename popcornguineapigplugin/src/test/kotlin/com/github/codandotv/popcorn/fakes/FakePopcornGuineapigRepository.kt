package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.data.report.PopcornGuineapigReportException
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport

internal val fakePopcornGuineapigRepository = object : PopcornGuineapigRepository {
    override fun exportReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>,
    ) = Unit
}

internal val fakePopcornGuineapigRepositoryWithError = object : PopcornGuineapigRepository {
    override fun exportReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>
    ) {
        throw PopcornGuineapigReportException("/users/moro/documents/svn/myreport.md")
    }
}
