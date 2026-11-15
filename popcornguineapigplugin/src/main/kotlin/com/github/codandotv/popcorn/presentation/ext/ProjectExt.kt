package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.identityKey
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
 * declaration.
 *
 * The identity of a declaration is its `identityKey`: the Gradle project path when known, the
 * lowercased module name otherwise. A coordinate-only declaration whose name matches the leaf
 * name of a declared project path is folded into that project declaration, so
 * `project(":car:data")` and `com.popcorn:data` count once. The project declaration wins and
 * keeps the position of the first declaration, so declaration order does not change the result.
 */
internal fun List<InternalDependenciesMetadata>.distinctDependencies(): List<InternalDependenciesMetadata> {
    // Leaf name -> project path, so a coordinate-only declaration can be folded onto the project
    // declaration of the same module even when the coordinate-only declaration comes first.
    val projectPathByLeafName = mutableMapOf<String, String>()
    forEach { dependency ->
        val path = dependency.modulePath ?: return@forEach
        val leafName = path.substringAfterLast(':').lowercase()
        if (!projectPathByLeafName.containsKey(leafName)) {
            projectPathByLeafName[leafName] = path
        }
    }

    val distinct = linkedMapOf<String, InternalDependenciesMetadata>()
    forEach { dependency ->
        val identity = dependency.modulePath
            ?: projectPathByLeafName[dependency.moduleName.lowercase()]
            ?: dependency.identityKey
        val collected = distinct[identity]
        val projectPathReplacesCoordinate =
            dependency.modulePath != null && collected?.modulePath == null

        if (collected == null || projectPathReplacesCoordinate) {
            distinct[identity] = dependency
        }
    }

    return distinct.values.toList()
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
