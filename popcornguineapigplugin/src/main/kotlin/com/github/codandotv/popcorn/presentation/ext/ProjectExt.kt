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
    // A project dependency is internal whatever its group says, so the group is only needed to
    // recognize modules declared by coordinates. Unset means "detect by type alone".
    val internalGroupName = groupName?.trim()?.takeIf { it.isNotEmpty() }

    logger.popcornLoggerDebug(
        internalGroupName?.let { "Detecting internal dependencies by type or group [$it]" }
            ?: "Detecting internal dependencies by type"
    )

    project.configurations.onEach { conf ->
        if (conf.name in configurationNames) {
            logger.popcornLoggerDebug("Checking configuration [${conf.name}]")

            conf.dependencies.forEach { safeDep ->
                logger.popcornLoggerDebug("PopcornGp: ${safeDep.group}:${safeDep.name}")

                safeDep.toInternalDependencyOrNull(internalGroupName)?.let { internalDependency ->
                    logger.popcornLoggerDebug(
                        "PopcornGp: Dependency ${safeDep.name} is internal at " +
                                (internalDependency.modulePath ?: "unknown path")
                    )
                    internalProjectDependencies.add(internalDependency)
                }
            }
        }
    }

    val distinctDependencies = internalProjectDependencies.distinctDependencies()

    logger.popcornLoggerDebug("Internal deps: $distinctDependencies of ${project.path}")
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

/**
 * A declaration is internal when Gradle knows it as a project dependency, whatever `group` the
 * target project sets: the type is exact, the group is a name.
 *
 * A dependency declared only by coordinates (`com.popcorn:data:1.0`) has no Gradle project behind
 * it, so it is internal only when it shares the explicitly configured internal group, compared on
 * whole group segments so `com.popcorn` matches `com.popcorn` and `com.popcorn.data`, never
 * `com.popcornx`.
 */
internal fun Dependency.toInternalDependencyOrNull(
    internalGroupName: String?,
): InternalDependenciesMetadata? = when {
    this is ProjectDependency -> InternalDependenciesMetadata(
        group = group,
        moduleName = name,
        modulePath = path,
    )

    internalGroupName != null && group.isWithinGroup(internalGroupName) -> InternalDependenciesMetadata(
        group = group,
        moduleName = name,
        modulePath = null,
    )

    else -> null
}

/** `com.popcorn` matches `com.popcorn` and `com.popcorn.data`, never `com.popcornx`. */
private fun String?.isWithinGroup(internalGroupName: String): Boolean {
    if (this == null) return false

    return this == internalGroupName || startsWith("$internalGroupName.")
}
