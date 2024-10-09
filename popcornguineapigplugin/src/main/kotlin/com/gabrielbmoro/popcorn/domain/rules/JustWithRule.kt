package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.output.ArchitectureViolationError
import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata

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
                message = "JustWithRule: This module should depends on $justWith",
                rule = this
            )
        }

        return null
    }
}
