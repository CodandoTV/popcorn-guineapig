package com.github.codandotv.popcorn.domain.output

import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

data class ArchitectureViolationError(
    val message: String,
    val rule: PopcornGuineaPigRule,
    val affectedRelationship: InternalDependenciesMetadata?
) {
    override fun toString(): String {
        return message
    }
}
