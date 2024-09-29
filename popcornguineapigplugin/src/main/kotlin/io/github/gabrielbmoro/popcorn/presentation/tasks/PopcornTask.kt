package io.github.gabrielbmoro.popcorn.presentation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import io.github.gabrielbmoro.popcorn.domain.entity.CheckResult
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import io.github.gabrielbmoro.popcorn.domain.entity.TargetModule
import io.github.gabrielbmoro.popcorn.domain.CheckArchitectureUseCase
import io.github.gabrielbmoro.popcorn.domain.GetRightConfigurationNameUseCase
import io.github.gabrielbmoro.popcorn.presentation.ext.internalProjectDependencies
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input

open class PopcornTask : DefaultTask() {

    private val checkArcUseCase = CheckArchitectureUseCase()
    private val getRightConfigurationNameUseCase = GetRightConfigurationNameUseCase()

    @Input
    lateinit var configuration: PopcornConfiguration

    @TaskAction
    fun process() {
        val internalProjectDependencies = project.internalProjectDependencies(
            configurationName = getRightConfigurationNameUseCase.execute(configuration.project.type),
        )

        val targetModule = TargetModule(
            moduleName = project.name,
            internalDependencies = internalProjectDependencies
        )

        val result = checkArcUseCase.execute(
            configuration = configuration,
            targetModule = targetModule
        )

        if (result is CheckResult.Failure) {
            throw IllegalStateException(result.errorMessage)
        }

        logger.log(LogLevel.INFO, "$targetModule")
    }
}