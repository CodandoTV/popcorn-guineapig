package com.gabrielbmoro.popcorn.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopCornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.TargetModule
import com.gabrielbmoro.popcorn.domain.usecase.CheckArchitectureUseCase
import org.gradle.api.tasks.Input

open class PopcornTask : DefaultTask() {

    private val checkArcUseCase = CheckArchitectureUseCase()

    @Input
    lateinit var configuration: PopCornConfiguration

    @TaskAction
    fun process() {
        val internalProjectDependencies = getInternalProjectDependencies()

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

        println("Check: $targetModule")
    }


    private fun getInternalProjectDependencies(): List<String> {
        val configurationTarget = when (configuration.project.type) {
            "android", "java" -> "implementation"
            "kmp" -> "commonMainImplementation"
            else -> throw IllegalStateException(
                "Project type not recognized. " +
                        "Please specify valid project types, such as: " +
                        "android, java, kmp"
            )
        }
        val internalProjectDependencies = mutableListOf<String>()
        project.configurations.onEach { conf ->
            if (conf.name == configurationTarget) {
                conf.dependencies.map {
                    if (it.group == configuration.project.group) {
                        internalProjectDependencies.add(it.name)
                    }
                }
            }
        }
        return internalProjectDependencies
    }
}