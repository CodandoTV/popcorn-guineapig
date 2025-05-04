package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.input.configurationName
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.report.ReportInfo
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.presentation.ext.internalProjectDependencies
import com.github.codandotv.popcorn.presentation.ext.logMessage
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerError
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerInfo
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerWarn
import com.github.codandotv.popcorn.presentation.ext.toErrorMessage
import org.gradle.api.logging.Logger
import kotlin.collections.forEach
import kotlin.reflect.KClass

typealias GradleProject = org.gradle.api.Project

internal class PopcornTaskHelper(
    private val checkArcUseCase: CheckArchitectureUseCase,
    private val generateReportUseCase: GenerateReportUseCase,
    private val logger: Logger,
    private val groupName: String?,
) {
    fun evaluate(
        gradleProject: GradleProject,
        rules: List<PopcornGuineaPigRule>,
        projectType: ProjectType,
        errorReportEnabled: Boolean,
        skippedRules: List<KClass<*>>,
    ) {
        logger.popcornLoggerInfo("Process popcorn task over ${gradleProject.displayName}")

        val internalProjectDependencies = gradleProject.internalProjectDependencies(
            configurationName = projectType.configurationName(),
            groupName = groupName
        )

        val targetModule = TargetModule(
            moduleName = gradleProject.displayName,
            internalDependencies = internalProjectDependencies
        )

        val result = checkArcUseCase.execute(
            rules = rules,
            internalDependencies = targetModule.internalDependencies,
        )

        logger.popcornLoggerInfo(targetModule.logMessage())

        if (result is CheckResult.Failure) {
            val (skippedErrors, internalErrors) = result.errors.partition {
                skippedRules.contains(it.rule::class)
            }

            logSkippedErrors(
                skippedErrors = skippedErrors,
                targetModule = targetModule
            )

            logErrors(
                errors = internalErrors,
                targetModule = targetModule,
            )

            generateReportIfNecessary(
                shouldGenerateErrorReport = errorReportEnabled && internalErrors.isNotEmpty(),
                targetModule = targetModule,
                result = result,
                skippedRules = skippedRules,
            )

            triggerErrorIfNecessary(internalErrors)
        }

        logger.popcornLoggerInfo("$targetModule")
    }

    private fun logSkippedErrors(
        skippedErrors: List<ArchitectureViolationError>,
        targetModule: TargetModule,
    ) {
        skippedErrors.forEach { skippedRule ->
            logger.popcornLoggerWarn(
                "${targetModule.moduleName} " +
                        "has bypassed rule ${skippedRule.rule::class.simpleName}"
            )
        }
    }

    private fun triggerErrorIfNecessary(errors: List<ArchitectureViolationError>) {
        errors.toErrorMessage()?.let {
            error(it)
        }
    }

    private fun logErrors(
        errors: List<ArchitectureViolationError>,
        targetModule: TargetModule,
    ) {
        errors.forEach { error ->
            logger.popcornLoggerError(
                "${targetModule.moduleName} is violating the rule " +
                        error.rule::class.simpleName
                        + "(${error.message})"
            )
        }
    }

    private fun generateReportIfNecessary(
        shouldGenerateErrorReport: Boolean,
        skippedRules: List<KClass<*>>,
        targetModule: TargetModule,
        result: CheckResult,
    ) {
        if (shouldGenerateErrorReport) {
            logger.popcornLoggerInfo("Generating the error report...")
            val result = runCatching {
                generateReportUseCase.execute(
                    reportInfo = ReportInfo(
                        targetModule = targetModule,
                        skippedRules = skippedRules,
                        checkResult = result,
                    )
                )
            }

            if (result.isFailure) {
                logger.popcornLoggerError("Something went wrong trying to generate the error report.")
            }
        }
    }
}
