package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.output.ArchitectureViolationError
import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata

class NoDependencyRule : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        if (deps.isNotEmpty()) {
            return ArchitectureViolationError(
                message = "NoDependencyRule: This module should not have dependencies",
                rule = this
            )
        }

        return null
    }
}
