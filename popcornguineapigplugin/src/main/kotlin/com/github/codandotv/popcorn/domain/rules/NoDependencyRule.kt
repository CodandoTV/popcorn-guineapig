package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata

public class NoDependencyRule : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        if (deps.isNotEmpty()) {
            return ArchitectureViolationError(
                message = "This module should not have dependencies",
                rule = this,
                affectedRelationship = deps.firstOrNull(),
            )
        }

        return null
    }
}
