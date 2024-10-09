package com.gabrielbmoro.popcorn.presentation.ext

import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

internal fun Project.internalProjectDependencies(
    configurationName: String,
    groupName: String?,
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()
    val projectGroupName = groupName ?: project.rootProject.name

    logger.log(LogLevel.INFO, "PopcornGp: Project group name is $projectGroupName")

    project.configurations.onEach { conf ->
        if (conf.name == configurationName) {
            logger.log(LogLevel.INFO, "PopcornGp: Checking configuration [${conf.name}, $configurationName]")

            conf.dependencies.map { dep ->
                dep?.let { safeDep ->
                    logger.log(LogLevel.INFO, "PopcornGp: ${safeDep.group}:${safeDep.name} <--> $projectGroupName")
                    if (safeDep.group?.contains(projectGroupName) == true) {

                        logger.log(LogLevel.INFO, "PopcornGp: Dependency ${dep.name} is internal")

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

    logger.log(LogLevel.INFO, "PopcornGp: Internal deps: $internalProjectDependencies of $projectGroupName")
    return internalProjectDependencies
}
