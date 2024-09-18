package com.gabrielbmoro.popcorn.presentation.ext

import com.gabrielbmoro.popcorn.domain.entity.InternalDependenciesMetadata
import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import org.gradle.api.Project

internal fun Project.internalProjectDependencies(
    configuration: PopcornConfiguration
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()

    val configurationTarget = configuration.project.type.configurationTarget

    project.configurations.onEach { conf ->
        if (conf.name == configurationTarget) {
            conf.dependencies.map { dep ->
                dep?.let { safeDep ->
                    if (safeDep.group?.contains(configuration.project.group) == true) {
                        internalProjectDependencies.add(
                            InternalDependenciesMetadata(
                                group = safeDep.group,
                                moduleName = safeDep.name
                            )
                        )
                    }
                }
            }
        }
    }
    return internalProjectDependencies
}
