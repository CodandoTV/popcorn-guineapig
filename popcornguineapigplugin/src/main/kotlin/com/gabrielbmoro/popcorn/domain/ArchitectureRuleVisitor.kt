package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.*

class ArchitectureRuleVisitorImpl(
    private val targetModule: TargetModule,
    private val sortedInternalProjectDependencies: List<InternalDependenciesMetadata>
) {
    fun doForDoNotWithRule(notWithRule: PopcornDoNotWithRule) {
        println("Checking $notWithRule")

        notWithRule.notWith.forEach { notWithRuleItem ->
            sortedInternalProjectDependencies.forEach { internalDependencyItem ->
                if (notWithRuleItem.toRegex().matches(internalDependencyItem.moduleName)) {
                    error(
                        "${targetModule.moduleName} has a not allowed dependency: ${internalDependencyItem.moduleName}"
                    )
                }
            }
        }
    }

    fun doNoRelationshipRule(noRelationShipRule: PopcornNoRelationShipRule) {
        println("Checking $noRelationShipRule")

        if (sortedInternalProjectDependencies.isNotEmpty()) {
            error(
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
            error(
                "${targetModule.moduleName} should only have deps " +
                        "with ${justWithRule.with}"
            )
        }
    }
}
