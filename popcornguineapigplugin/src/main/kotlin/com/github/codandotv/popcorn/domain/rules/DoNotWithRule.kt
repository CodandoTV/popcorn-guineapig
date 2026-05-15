package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata

public class DoNotWithRule(
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
