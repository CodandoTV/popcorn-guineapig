package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.*

internal class ArchitectureRuleVisitor(
    private val targetModule: TargetModule,
    private val sortedInternalProjectDependencies: List<InternalDependenciesMetadata>
) {
    fun doForDoNotWithRule(notWithRule: PopcornDoNotWithRule): ArchitectureViolationError? {
        println("Checking $notWithRule")

        notWithRule.notWith.forEach { notWithRuleItem ->
            sortedInternalProjectDependencies.forEach { internalDependencyItem ->
                if (notWithRuleItem.toRegex().matches(internalDependencyItem.moduleName)) {
                    return ArchitectureViolationError(
                        targetModule = targetModule,
                        rule = notWithRule
                    )
                }
            }
        }

        return null
    }

    fun doNoRelationshipRule(noRelationShipRule: PopcornNoRelationShipRule): ArchitectureViolationError? {
        println("Checking $noRelationShipRule")

        if (sortedInternalProjectDependencies.isNotEmpty()) {
            return ArchitectureViolationError(
                targetModule = targetModule,
                rule = noRelationShipRule
            )
        }

        return null
    }

    fun doJustWithRule(justWithRule: PopcornJustWithRule): ArchitectureViolationError? {
        println("Checking $justWithRule")

        val sortedInternalProjectDependencyModuleNames = sortedInternalProjectDependencies.map {
            it.moduleName
        }

        val sortedTargetRuleAllowedDep = justWithRule.with.sorted()
        if (sortedInternalProjectDependencyModuleNames != sortedTargetRuleAllowedDep) {
            return ArchitectureViolationError(
                targetModule = targetModule,
                rule = justWithRule
            )
        }

        return null
    }
}
