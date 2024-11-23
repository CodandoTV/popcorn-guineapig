package com.github.codandotv.popcorn.domain.metadata

data class TargetModule(
    val moduleName: String,
    val internalDependencies: List<InternalDependenciesMetadata>
)
