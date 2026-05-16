package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata

public class JustWithRule(
    private val justWith: List<String>
) : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        val internalProjectDependenciesSortedByModuleName = deps.sortedBy { it.moduleName }
        val internalProjectDependencyModuleNames = internalProjectDependenciesSortedByModuleName.map { it.moduleName }

        val justWithSorted = justWith.sorted()
        if (internalProjectDependencyModuleNames != justWithSorted) {
            val affectedRelationships = internalProjectDependenciesSortedByModuleName.filterNot {
                justWithSorted.contains(it.moduleName)
            }

            return ArchitectureViolationError(
                message = "This module should depends on $justWith",
                rule = this,
                affectedRelationship = affectedRelationships
            )
        }

        return null
    }
}
