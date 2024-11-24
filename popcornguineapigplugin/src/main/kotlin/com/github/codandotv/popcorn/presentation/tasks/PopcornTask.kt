package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.GetRightConfigurationNameUseCase
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
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
import org.koin.core.Koin
import kotlin.reflect.KClass

open class PopcornTask : DefaultTask() {

    private lateinit var checkArcUseCase: CheckArchitectureUseCase
    private lateinit var getRightConfigurationNameUseCase: GetRightConfigurationNameUseCase
    private lateinit var generateReportUseCase: GenerateReportUseCase

    @Input
    lateinit var configuration: PopcornConfiguration

    @Input
    lateinit var skippedRules: List<KClass<*>>

    @Input
    var hasReportEnabled: Boolean = false

    fun start(koin: Koin) {
        checkArcUseCase = koin.get<CheckArchitectureUseCase>()
        getRightConfigurationNameUseCase = koin.get<GetRightConfigurationNameUseCase>()
        generateReportUseCase = koin.get<GenerateReportUseCase>()
    }

    @TaskAction
    fun process() {
        logger.popcornLoggerInfo("Process popcorn task over ${project.name.orEmpty()}")

        val internalProjectDependencies = project.internalProjectDependencies(
            configurationName = getRightConfigurationNameUseCase.execute(configuration.project.type),
            groupName = configuration.project.groupName
        )

        val targetModule = TargetModule(
            moduleName = project.name,
            internalDependencies = internalProjectDependencies
        )

        val result = checkArcUseCase.execute(
            configuration = configuration,
            targetModule = targetModule,
        )

        logger.popcornLoggerInfo(targetModule.logMessage())

        if (result is CheckResult.Failure) {
            val (skippedErrors, errors) = result.errors.partition {
                skippedRules.contains(it.rule::class)
            }

            logSkippedErrors(skippedErrors, targetModule)

            logErrors(errors, targetModule)

            generateReportIfNecessary(targetModule, result)

            triggerErrorIfNecessary(errors)
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
        targetModule: TargetModule,
        result: CheckResult
    ) {
        if (hasReportEnabled) {
            logger.popcornLoggerInfo("Generating the report...")
            generateReportUseCase.execute(
                reportInfo = ReportInfo(
                    targetModule = targetModule,
                    configuration = configuration,
                    skippedRules = skippedRules,
                    checkResult = result
                )
            )
        }
    }
}
