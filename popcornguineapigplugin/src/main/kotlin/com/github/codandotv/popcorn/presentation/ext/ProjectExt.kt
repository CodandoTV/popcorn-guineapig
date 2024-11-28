package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import org.gradle.api.Project

internal fun Project.internalProjectDependencies(
    configurationName: String,
    groupName: String?,
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()
    val projectGroupName = groupName ?: project.rootProject.name

    logger.popcornLoggerDebug("Project group name is $projectGroupName")

    project.configurations.onEach { conf ->
        if (conf.name == configurationName) {
            logger.popcornLoggerDebug("Checking configuration [${conf.name}, $configurationName]")

            conf.dependencies.map { dep ->
                dep?.let { safeDep ->
                    logger.popcornLoggerDebug("PopcornGp: ${safeDep.group}:${safeDep.name} <--> $projectGroupName")
                    if (safeDep.group?.contains(projectGroupName) == true) {

                        logger.popcornLoggerDebug("PopcornGp: Dependency ${dep.name} is internal")

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

    logger.popcornLoggerDebug("Internal deps: $internalProjectDependencies of $projectGroupName")
    return internalProjectDependencies
}
