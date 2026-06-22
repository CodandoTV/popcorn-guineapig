package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.ModuleMetric

internal val fakePopcornGuineapigRepository = object : PopcornGuineapigRepository {
    override fun exportErrorReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>,
    ) = Unit

    override fun exportMetricsReport(
        reportPath: String,
        metrics: List<ModuleMetric>
    ) {
        error("Should not being called")
    }

    override fun installSkill(
        skillOutputDir: String,
        skillName: String,
    ) = Unit
}

internal val fakePopcornGuineapigRepositoryWithError = object : PopcornGuineapigRepository {
    override fun exportErrorReport(
        reportPath: String,
        architectureViolationReportData: List<ArchitectureViolationReport>
    ) {
        throw RuntimeException("Something went wrong with the report")
    }

    override fun exportMetricsReport(reportPath: String, metrics: List<ModuleMetric>) {
        error("Should not being called")
    }

    override fun installSkill(
        skillOutputDir: String,
        skillName: String,
    ) = Unit
}

internal fun fakePopcornGuineapigRepositoryWithCallbacks(
    onErrorReportCallback: () -> Unit = {},
    onMetricsReportCallback: () -> Unit = {},
    onInstallSkillCallback: () -> Unit = {},
) : PopcornGuineapigRepository {
    return object : PopcornGuineapigRepository{
        override fun exportErrorReport(
            reportPath: String,
            architectureViolationReportData: List<ArchitectureViolationReport>
        ) {
            onErrorReportCallback()
        }

        override fun exportMetricsReport(
            reportPath: String,
            metrics: List<ModuleMetric>
        ) {
            onMetricsReportCallback()
        }

        override fun installSkill(
            skillOutputDir: String,
            skillName: String,
        ) {
            onInstallSkillCallback()
        }
    }
}
