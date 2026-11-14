package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ProjectDependency

internal fun Project.internalProjectDependencies(
    configurationNames: List<String>,
    groupName: String?,
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()
    val projectGroupName = groupName ?: project.rootProject.name

    logger.popcornLoggerDebug("Project group name is $projectGroupName")

    project.configurations.onEach { conf ->
        configurationNames.forEach { configurationName ->
            if (conf.name == configurationName) {
                logger.popcornLoggerDebug("Checking configuration [${conf.name}, $configurationName]")

                conf.dependencies.forEach { safeDep ->
                    logger.popcornLoggerDebug("PopcornGp: ${safeDep.group}:${safeDep.name} <--> $projectGroupName")

                    safeDep.toInternalDependencyOrNull(projectGroupName)?.let { internalDependency ->
                        logger.popcornLoggerDebug(
                            "PopcornGp: Dependency ${safeDep.name} is internal at " +
                                    (internalDependency.modulePath ?: "unknown path")
                        )
                        internalProjectDependencies.add(internalDependency)
                    }
                }
            }
        }
    }

    logger.popcornLoggerDebug("Internal deps: $internalProjectDependencies of $projectGroupName")
    return internalProjectDependencies
}

internal fun Dependency.toInternalDependencyOrNull(
    projectGroupName: String,
): InternalDependenciesMetadata? {
    if (group?.contains(projectGroupName) != true) {
        return null
    }

    return InternalDependenciesMetadata(
        group = group,
        moduleName = name,
        modulePath = (this as? ProjectDependency)?.path,
    )
}
