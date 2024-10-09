package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.output.ArchitectureViolationError
import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata

class DoNotWithRule(
    private val notWith: List<String>
) : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        notWith.forEach { notWithRuleItem ->
            deps.forEach { internalDependencyItem ->
                if (notWithRuleItem.toRegex().matches(internalDependencyItem.moduleName)) {
                    return ArchitectureViolationError(
                        rule = this,
                        message = "DoNotWithRule: This module should not depends on [$deps]"
                    )
                }
            }
        }
        return null
    }
}
