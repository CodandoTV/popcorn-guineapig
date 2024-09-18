package com.gabrielbmoro.popcorn.presentation.ext

import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import org.gradle.api.Project

internal fun Project.internalProjectDependencies(
    configuration: PopcornConfiguration
): List<String> {
    val internalProjectDependencies = mutableListOf<String>()

    val configurationTarget = configuration.project.type.configurationTarget

    project.configurations.onEach { conf ->
        if (conf.name == configurationTarget) {
            conf.dependencies.map {
                if (it.group?.contains(configuration.project.group) == true) {
                    internalProjectDependencies.add(it.name)
                }
            }
        }
    }
    return internalProjectDependencies
}
