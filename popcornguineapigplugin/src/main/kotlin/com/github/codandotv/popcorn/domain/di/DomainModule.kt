package com.github.codandotv.popcorn.domain.di

import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCaseImpl
import com.github.codandotv.popcorn.domain.usecases.GetRightConfigurationNameUseCase
import com.github.codandotv.popcorn.domain.usecases.GetRightConfigurationNameUseCaseImpl
import org.koin.dsl.module

internal val domainModule = module {
    factory<CheckArchitectureUseCase> {
        CheckArchitectureUseCaseImpl()
    }

    factory<GetRightConfigurationNameUseCase> {
        GetRightConfigurationNameUseCaseImpl()
    }
}