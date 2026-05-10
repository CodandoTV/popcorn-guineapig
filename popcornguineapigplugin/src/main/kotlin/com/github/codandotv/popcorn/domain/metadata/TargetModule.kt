package com.github.codandotv.popcorn.domain.metadata

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

internal data class TargetModule(
    val moduleName: String,
    val internalDependencies: List<InternalDependenciesMetadata>,
    val rules: List<PopcornGuineaPigRule>,
)
