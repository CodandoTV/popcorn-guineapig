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

    val distinctDependencies = internalProjectDependencies.distinctDependencies()

    logger.popcornLoggerDebug("Internal deps: $distinctDependencies of $projectGroupName")
    return distinctDependencies
}

/**
 * Keeps one entry per module. A KMP module can declare the same project in more than one
 * source set (for example `commonMain` and `androidMain`), which yields one entry per
 * declaration. The identity is the Gradle project path when it is known and the module name
 * otherwise, the same rule the metrics use case applies when it matches incoming dependencies
 * to the modules that declared them.
 */
internal fun List<InternalDependenciesMetadata>.distinctDependencies(): List<InternalDependenciesMetadata> =
    distinctBy { dependency -> dependency.modulePath ?: dependency.moduleName.lowercase() }

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
