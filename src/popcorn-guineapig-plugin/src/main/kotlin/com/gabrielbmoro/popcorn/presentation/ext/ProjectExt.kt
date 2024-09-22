package com.gabrielbmoro.popcorn.presentation.ext

import com.gabrielbmoro.popcorn.domain.entity.InternalDependenciesMetadata
import org.gradle.api.Project

internal fun Project.internalProjectDependencies(
    configurationName: String,
    projectGroupName: String,
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()

    val configurationTarget = configurationName

    project.configurations.onEach { conf ->
        if (conf.name == configurationTarget) {
            conf.dependencies.map { dep ->
                dep?.let { safeDep ->
                    if (safeDep.group?.contains(projectGroupName) == true) {
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
