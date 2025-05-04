package com.github.codandotv.popcorn

import com.github.codandotv.popcorn.data.PopcornGuineapigRepositoryImpl
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCaseImpl

class DependencyFactory(reportPath: String) {

    private val repository = PopcornGuineapigRepositoryImpl(
        reportPath = reportPath,
        reportDataSource = ReportDataSource()
    )

    fun provideCheckArchitectureUseCase(): CheckArchitectureUseCase {
        return CheckArchitectureUseCaseImpl()
    }

    fun provideGenerateReportUseCase(): GenerateReportUseCase {
        return GenerateReportUseCaseImpl(
            repository = repository
        )
    }
}
