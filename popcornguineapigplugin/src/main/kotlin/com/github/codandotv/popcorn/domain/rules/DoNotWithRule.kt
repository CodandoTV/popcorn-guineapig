package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata

class DoNotWithRule(
    private val notWith: List<String>
) : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        notWith.forEach { notWithRuleItem ->
            deps.forEach { internalDependencyItem ->
                if (notWithRuleItem.toRegex().matches(internalDependencyItem.moduleName)) {
                    return ArchitectureViolationError(
                        rule = this,
                        message = "This module should not depends on [$notWith]",
                        affectedRelationship = internalDependencyItem
                    )
                }
            }
        }
        return null
    }
}
