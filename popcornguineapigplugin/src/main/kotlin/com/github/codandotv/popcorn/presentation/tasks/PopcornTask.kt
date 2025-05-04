package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.DependencyFactory
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.domain.input.configurationName
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.report.ReportInfo
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.presentation.ext.internalProjectDependencies
import com.github.codandotv.popcorn.presentation.ext.logMessage
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerError
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerInfo
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerWarn
import com.github.codandotv.popcorn.presentation.ext.toErrorMessage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import kotlin.reflect.KClass

open class PopcornTask : DefaultTask() {

    private lateinit var checkArcUseCase: CheckArchitectureUseCase
    private lateinit var generateReportUseCase: GenerateReportUseCase

    @Input
    lateinit var configuration: PopcornConfiguration

    @Input
    lateinit var skippedRules: List<KClass<*>>

    private var _errorReportEnabled: Boolean = false

    fun start(
        errorReportEnabled: Boolean,
        dependencyFactory: DependencyFactory
    ) {
        _errorReportEnabled = errorReportEnabled
        checkArcUseCase = dependencyFactory.provideCheckArchitectureUseCase()
        generateReportUseCase = dependencyFactory.provideGenerateReportUseCase()
    }

    @TaskAction
    fun process() {
        logger.popcornLoggerInfo("Process popcorn task over ${project.displayName}")

        val internalProjectDependencies = project.internalProjectDependencies(
            configurationName = configuration.project.type.configurationName(),
            groupName = configuration.project.groupName
        )

        val targetModule = TargetModule(
            moduleName = project.displayName,
            internalDependencies = internalProjectDependencies
        )

        val result = checkArcUseCase.execute(
            rules = configuration.rules,
            targetModule = targetModule,
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
                shouldGenerateErrorReport = _errorReportEnabled && internalErrors.isNotEmpty(),
                targetModule = targetModule,
                result = result
            )

            triggerErrorIfNecessary(internalErrors)
        }

        logger.popcornLoggerInfo("$targetModule")
    }

    private fun logSkippedErrors(
        skippedErrors: List<ArchitectureViolationError>,
        targetModule: TargetModule
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
        targetModule: TargetModule
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
        targetModule: TargetModule,
        result: CheckResult
    ) {
        if (shouldGenerateErrorReport) {
            logger.popcornLoggerInfo("Generating the error report...")
            val result = runCatching {
                generateReportUseCase.execute(
                    reportInfo = ReportInfo(
                        targetModule = targetModule,
                        configuration = configuration,
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
