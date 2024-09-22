package com.gabrielbmoro.popcorn.presentation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.TargetModule
import com.gabrielbmoro.popcorn.domain.CheckArchitectureUseCase
import com.gabrielbmoro.popcorn.domain.GetRightConfigurationNameUseCase
import com.gabrielbmoro.popcorn.presentation.ext.internalProjectDependencies
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
            projectGroupName = configuration.project.group
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