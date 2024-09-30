package com.gabrielbmoro.popcorn.presentation.ext

import com.gabrielbmoro.popcorn.domain.entity.InternalDependenciesMetadata
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

internal fun Project.internalProjectDependencies(
    configurationName: String,
): List<InternalDependenciesMetadata> {
    val internalProjectDependencies = mutableListOf<InternalDependenciesMetadata>()
    val projectGroupName = project.rootProject.name

    logger.log(LogLevel.INFO, "PopcornGp: Project group name is $projectGroupName")

    project.configurations.onEach { conf ->
        logger.log(LogLevel.INFO, "PopcornGp: Checking configuration ${conf.name}")

        if (conf.name == configurationName) {
            logger.log(LogLevel.INFO, "PopcornGp: Checking configuration [${conf.name}, $configurationName]")

            conf.dependencies.map { dep ->
                logger.log(LogLevel.INFO, "PopcornGp: Checking dependency ${dep.name}")

                dep?.let { safeDep ->
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
