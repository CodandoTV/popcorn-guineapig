package com.github.codandotv.popcorn.domain.metadata

internal data class TargetModule(
    val moduleName: String,
    val internalDependencies: List<InternalDependenciesMetadata>
)
