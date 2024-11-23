package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata

class NoDependencyRule : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        if (deps.isNotEmpty()) {
            return ArchitectureViolationError(
                message = "This module should not have dependencies",
                rule = this
            )
        }

        return null
    }
}
