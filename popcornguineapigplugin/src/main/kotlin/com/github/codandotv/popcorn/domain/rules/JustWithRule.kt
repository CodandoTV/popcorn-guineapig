package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata

class JustWithRule(
    private val justWith: List<String>
) : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        val sortedInternalProjectDependencyModuleNames = deps.map {
            it.moduleName
        }

        val sortedTargetRuleAllowedDep = justWith.sorted()
        if (sortedInternalProjectDependencyModuleNames != sortedTargetRuleAllowedDep) {
            return ArchitectureViolationError(
                message = "This module should depends on $justWith",
                rule = this
            )
        }

        return null
    }
}
