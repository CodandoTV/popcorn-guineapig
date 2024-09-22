package com.gabrielbmoro.popcorn.presentation.ext

import com.android.build.gradle.internal.scope.ProjectInfo.Companion.getBaseName
import com.gabrielbmoro.popcorn.domain.entity.InternalDependenciesMetadata
import org.gradle.api.Project

internal fun Project.internalProjectDependencies(
    configurationName: String
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()
    val projectGroupName = project.rootProject.name
    project.configurations.onEach { conf ->
        if (conf.name == configurationName) {
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
