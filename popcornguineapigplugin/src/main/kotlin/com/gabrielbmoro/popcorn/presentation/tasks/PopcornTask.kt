package com.gabrielbmoro.popcorn.presentation.tasks

import com.gabrielbmoro.popcorn.domain.CheckArchitectureUseCase
import com.gabrielbmoro.popcorn.domain.GetRightConfigurationNameUseCase
import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.TargetModule
import com.gabrielbmoro.popcorn.presentation.ext.internalProjectDependencies
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.logging.LogLevel
import org.gradle.api.tasks.Input

open class PopcornTask : DefaultTask() {

    private val checkArcUseCase = CheckArchitectureUseCase()
    private val getRightConfigurationNameUseCase = GetRightConfigurationNameUseCase()

    @Input
    lateinit var configuration: PopcornConfiguration

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
            targetModule = targetModule
        )

        logger.log(LogLevel.INFO, "PopcornGp: Result of checking $targetModule --> $result")

        if (result is CheckResult.Failure) {
            logger.error(result.errorMessage)
            throw GradleException(result.errorMessage)
        }

        logger.log(LogLevel.INFO, "$targetModule")
    }
}