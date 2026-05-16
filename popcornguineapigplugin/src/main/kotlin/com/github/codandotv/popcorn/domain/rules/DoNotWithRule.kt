package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata

public class DoNotWithRule(
    private val notWith: List<String>
) : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        val affectedRelationships = mutableListOf<InternalDependenciesMetadata>()

        notWith.forEach { notWithRuleItem ->
            affectedRelationships.addAll(
                deps.filter { dep ->
                    notWithRuleItem.toRegex().matches(dep.moduleName)
                }
            )
        }

        if (affectedRelationships.isNotEmpty()) {
            return ArchitectureViolationError(
                rule = this,
                message = "This module should not depends on [$notWith]",
                affectedRelationship = affectedRelationships
            )
        } else {
            return null
        }
    }
}
