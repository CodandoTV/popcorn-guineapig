package com.github.codandotv.popcorn

import com.github.codandotv.popcorn.data.PopcornGuineapigRepositoryImpl
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCaseImpl

internal class DependencyFactory() {

    private val repository = PopcornGuineapigRepositoryImpl(
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
