package com.github.codandotv.popcorn

import com.github.codandotv.popcorn.data.PopcornGuineapigRepositoryImpl
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.usecases.AnalyseArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.AnalyseArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCaseImpl

internal object ServiceLocator {

    val repository: PopcornGuineapigRepository by lazy {
        PopcornGuineapigRepositoryImpl(
            reportDataSource = ReportDataSource()
        )
    }

    val checkArchitectureUseCase: CheckArchitectureUseCase by lazy {
        CheckArchitectureUseCaseImpl()
    }

    val analyseArchitectureUseCase: AnalyseArchitectureUseCase by lazy {
        AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = checkArchitectureUseCase,
            generateReportUseCase = generateReportUseCase,
        )
    }

    val generateReportUseCase: GenerateReportUseCase by lazy {
        GenerateReportUseCaseImpl(repository)
    }
}
