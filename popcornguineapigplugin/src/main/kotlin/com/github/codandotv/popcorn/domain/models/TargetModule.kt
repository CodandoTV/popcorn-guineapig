package com.github.codandotv.popcorn.domain.models

import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

internal data class TargetModule(
    val moduleName: String,
    val internalDependencies: List<InternalDependenciesMetadata>,
    val rules: List<PopcornGuineaPigRule>,
) {
    override fun toString(): String {
        val separator = "\n----------------------"
        val internalDepsReport = internalDependencies.map { it.moduleName }
            .reduceOrNull { acc, s ->
                "$acc,$s"
            } ?: ""

        return separator + "\nModule name: $moduleName\n" +
                "Internal dependencies: " + internalDepsReport + separator
    }
}
