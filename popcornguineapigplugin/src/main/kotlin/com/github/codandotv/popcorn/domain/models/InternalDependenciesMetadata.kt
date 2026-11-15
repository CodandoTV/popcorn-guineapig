package com.github.codandotv.popcorn.domain.models

public data class InternalDependenciesMetadata(
    val group: String?,
    val moduleName: String,
    val modulePath: String? = null,
)

/**
 * The identity of the module this entry references: the Gradle project path when the dependency is
 * a project dependency, the lowercased module name otherwise.
 *
 * Two entries sharing an identity are two declarations of the same module, so together they
 * contribute a single dependency edge.
 */
internal val InternalDependenciesMetadata.identityKey: String
    get() = modulePath ?: moduleName.lowercase()
