package com.gabrielbmoro.popcorn.presentation.tasks

import com.gabrielbmoro.popcorn.domain.usecases.CheckArchitectureUseCase
import com.gabrielbmoro.popcorn.domain.usecases.GetRightConfigurationNameUseCase
import com.gabrielbmoro.popcorn.domain.output.CheckResult
import com.gabrielbmoro.popcorn.domain.metadata.TargetModule
import com.gabrielbmoro.popcorn.domain.input.PopcornConfiguration
import com.gabrielbmoro.popcorn.presentation.ext.internalProjectDependencies
import com.gabrielbmoro.popcorn.presentation.ext.toErrorMessage
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input
import kotlin.reflect.KClass

open class PopcornTask : DefaultTask() {

    private val checkArcUseCase = CheckArchitectureUseCase()
    private val getRightConfigurationNameUseCase = GetRightConfigurationNameUseCase()

    @Input
    lateinit var configuration: PopcornConfiguration

    @Input
    lateinit var skippedRules: List<KClass<*>>

    @TaskAction
    fun process() {
        logger.log(LogLevel.INFO, "PopcornGp: Checking ${project.name}")

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

        logger.log(LogLevel.INFO, "PopcornGp: Result of checking $targetModule --> $result")

        if (result is CheckResult.Failure) {
            val (skippedErrors, errors) = result.errors.partition { skippedRules.contains(it.rule::class) }

            skippedErrors.forEach {
                logger.warn("Skipped --> $it")
            }

            errors.forEach {
                logger.error(it.toString())
            }

            errors.toErrorMessage()?.let {
                error(it)
            }
        }

        logger.log(LogLevel.INFO, "$targetModule")
    }
}
