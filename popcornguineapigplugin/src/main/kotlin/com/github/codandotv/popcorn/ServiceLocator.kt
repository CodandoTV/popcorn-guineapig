package com.github.codandotv.popcorn

import com.github.codandotv.popcorn.data.PopcornGuineapigRepositoryImpl
import com.github.codandotv.popcorn.data.SkillDataSource
import com.github.codandotv.popcorn.data.report.ReportDataSource
import com.github.codandotv.popcorn.domain.Logger
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.usecases.InstallSkillUseCase
import com.github.codandotv.popcorn.domain.usecases.InstallSkillUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.check.AnalyseArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.check.AnalyseArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.check.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.check.CheckArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.metric.CollectModuleMetricsUseCase
import com.github.codandotv.popcorn.domain.usecases.metric.CollectModuleMetricsUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.check.GenerateArchitectureViolationReport
import com.github.codandotv.popcorn.domain.usecases.check.GenerateArchitectureViolationReportImpl
import com.github.codandotv.popcorn.domain.usecases.metric.GenerateMetricsReportUseCase
import com.github.codandotv.popcorn.domain.usecases.metric.GenerateMetricsReportUseCaseImpl
import kotlin.math.log

internal object ServiceLocator {

    val repository: PopcornGuineapigRepository by lazy {
        PopcornGuineapigRepositoryImpl(
            reportDataSource = ReportDataSource(),
            skillDataSource = SkillDataSource(
                classLoader = PopcornGuineapigRepositoryImpl::class.java.classLoader,
            ),
        )
    }

    val checkArchitectureUseCase: CheckArchitectureUseCase by lazy {
        CheckArchitectureUseCaseImpl()
    }

    val generateArchitectureViolationReport: GenerateArchitectureViolationReport by lazy {
        GenerateArchitectureViolationReportImpl(repository)
    }

    val collectModuleMetricsUseCase: CollectModuleMetricsUseCase by lazy {
        CollectModuleMetricsUseCaseImpl()
    }

    fun provideAnalyseArchitectureUseCase(logger: Logger): AnalyseArchitectureUseCase {
        return AnalyseArchitectureUseCaseImpl(
            checkArchitectureUseCase = checkArchitectureUseCase,
            generateArchitectureViolationReport = generateArchitectureViolationReport,
            logger = logger,
        )
    }

    fun provideGenerateMetricsReportUseCase(logger: Logger): GenerateMetricsReportUseCase {
        return GenerateMetricsReportUseCaseImpl(
            repository = repository,
            collectModuleMetricsUseCase = collectModuleMetricsUseCase,
            logger = logger,
        )
    }

    fun provideInstallSkillUseCase(logger: Logger): InstallSkillUseCase {
        return InstallSkillUseCaseImpl(
            logger = logger,
            repository = repository,
        )
    }
}
