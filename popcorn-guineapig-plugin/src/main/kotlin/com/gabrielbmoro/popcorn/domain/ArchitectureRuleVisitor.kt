package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.InternalDependenciesMetadata
import com.gabrielbmoro.popcorn.domain.entity.PopcornDoNotWithRule
import com.gabrielbmoro.popcorn.domain.entity.PopcornJustWithRule
import com.gabrielbmoro.popcorn.domain.entity.PopcornNoRelationShipRule
import com.gabrielbmoro.popcorn.domain.entity.TargetModule

class ArchitectureRuleVisitorImpl(
    private val targetModule: TargetModule,
    private val sortedInternalProjectDependencies: List<InternalDependenciesMetadata>
) {
    fun doForDoNotWithRule(notWithRule: PopcornDoNotWithRule) {
        println("Checking $notWithRule")

        val isThereNotAllowedDep = sortedInternalProjectDependencies.any { internalDep ->
            notWithRule.notWith.contains(internalDep.moduleName)
        }
        if (isThereNotAllowedDep) {
            throw IllegalStateException(
                "${targetModule.moduleName} has a not allowed dependency"
            )
        }
    }

    fun doNoRelationshipRule(noRelationShipRule: PopcornNoRelationShipRule) {
        println("Checking $noRelationShipRule")

        if (sortedInternalProjectDependencies.isNotEmpty()) {
            throw IllegalStateException(
                "${targetModule.moduleName} should not have deps"
            )
        }
    }

    fun doJustWithRule(justWithRule: PopcornJustWithRule) {
        println("Checking $justWithRule")

        val sortedInternalProjectDependencyModuleNames = sortedInternalProjectDependencies.map {
            it.moduleName
        }

        val sortedTargetRuleAllowedDep = justWithRule.with.sorted()
        if (sortedInternalProjectDependencyModuleNames != sortedTargetRuleAllowedDep) {
            throw IllegalStateException(
                "${targetModule.moduleName} should only have deps " +
                        "with ${justWithRule.with}"
            )
        }
    }
}
