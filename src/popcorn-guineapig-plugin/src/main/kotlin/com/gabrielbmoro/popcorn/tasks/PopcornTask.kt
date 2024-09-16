package com.gabrielbmoro.popcorn.tasks

import com.gabrielbmoro.popcorn.domain.entity.ArcViolationRule
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.TargetModule
import com.gabrielbmoro.popcorn.domain.usecase.CheckArchitectureUseCase

open class PopcornTask : DefaultTask() {

    private val checkArcUseCase = CheckArchitectureUseCase()

    private val rules = listOf(
        ArcViolationRule.JustWith(
            targetModule = "data",
            justWith = listOf(
                "com/gabrielbmoro/popcorn/domain",
            ),
        ),
        ArcViolationRule.NoRelationship(
            targetModule = "com/gabrielbmoro/popcorn/domain"
        ),
        ArcViolationRule.NoRelationship(
            targetModule = "resources"
        ),
        ArcViolationRule.JustWith(
            targetModule = "designsystem",
            justWith = listOf("resources")
        ),
        ArcViolationRule.Feature(
            targetModule = "featureModule"
        ),
        ArcViolationRule.NoRelationship(
            targetModule = "platform",
        )
    )

    @TaskAction
    fun process() {
        val internalProjectDependencies = getInternalProjectDependencies()

        val targetModule = TargetModule(
            moduleName = project.name,
            isFeatureModule = isFeatureModule(),
            internalDependencies = internalProjectDependencies
        )

        val result = checkArcUseCase.execute(rules = rules, targetModule = targetModule)

        if (result is CheckResult.Failure) {
            throw IllegalStateException(result.errorMessage)
        }

        println("Check: $targetModule")
    }

    private fun isFeatureModule() = project.parent?.name == FEATURE_PARENT_MODULE_NAME

    private fun getInternalProjectDependencies(): List<String> {
        val internalProjectDependencies = mutableListOf<String>()
        project.configurations.onEach { conf ->
            if (conf.name == TARGET_CONFIGURATION_NAME) {
                conf.dependencies.map {
                    if (it.group == TARGET_GROUP_NAME) {
                        internalProjectDependencies.add(it.name)
                    }
                }
            }
        }
        return internalProjectDependencies
    }

    companion object {
        private const val TARGET_GROUP_NAME = "MovieDBApp"
        private const val TARGET_CONFIGURATION_NAME = "commonMainImplementation"
        private const val FEATURE_PARENT_MODULE_NAME = "feature"
    }
}