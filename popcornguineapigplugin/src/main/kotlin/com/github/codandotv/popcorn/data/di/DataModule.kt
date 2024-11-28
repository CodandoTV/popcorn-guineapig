package com.github.codandotv.popcorn.data.di

import com.github.codandotv.popcorn.data.PopcornGuineapigRepository
import com.github.codandotv.popcorn.data.PopcornGuineapigRepositoryImpl
import com.github.codandotv.popcorn.data.report.ReportDataSource
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val dataModule = module {
    single<ReportDataSource> {
        ReportDataSource()
    }

    single<PopcornGuineapigRepository> {
        PopcornGuineapigRepositoryImpl(
            reportDataSource = get(),
            reportPath = get(
                named(REPORT_PATH_KEY)
            )
        )
    }
}

internal const val REPORT_PATH_KEY = "reportPathKey"
