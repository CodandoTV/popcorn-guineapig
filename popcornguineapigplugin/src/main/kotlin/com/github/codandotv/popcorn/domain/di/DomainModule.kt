@file:Suppress("WildcardImport")

package com.github.codandotv.popcorn.domain.di

import com.github.codandotv.popcorn.domain.usecases.*
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.GetRightConfigurationNameUseCaseImpl
import org.koin.dsl.module

internal val domainModule = module {
    single<CheckArchitectureUseCase> {
        CheckArchitectureUseCaseImpl()
    }

    single<GetRightConfigurationNameUseCase> {
        GetRightConfigurationNameUseCaseImpl()
    }

    single<GenerateReportUseCase> {
        GenerateReportUseCaseImpl(
            repository = get()
        )
    }
}
