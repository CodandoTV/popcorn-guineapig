package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata

public class JustWithRule(
    private val justWith: List<String>
) : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        val affectedRelationships = mutableListOf<InternalDependenciesMetadata>()

        deps.forEach { dep ->
            val matchesAnyPattern = justWith.any { pattern ->
                pattern.toRegex().matches(dep.moduleName)
            }
            if (!matchesAnyPattern) {
                affectedRelationships.add(dep)
            }
        }

        return if (affectedRelationships.isNotEmpty()) {
            ArchitectureViolationError(
                message = "This module should depends on $justWith",
                rule = this,
                affectedRelationship = affectedRelationships
            )
        } else {
            null
        }
    }
}
