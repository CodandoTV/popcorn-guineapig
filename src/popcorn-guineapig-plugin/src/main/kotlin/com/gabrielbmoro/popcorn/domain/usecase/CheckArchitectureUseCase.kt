package com.gabrielbmoro.popcorn.domain.usecase

import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.TargetModule

class CheckArchitectureUseCase {

    fun execute(configuration: PopcornConfiguration, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies.sorted()

        configuration.rules.justWith.firstOrNull { rule -> rule.target == targetModule.moduleName }
            ?.let { targetRule ->
                val sortedTargetRuleAllowedDep = targetRule.with.sorted()
                if (sortedInternalProjectDependencies != sortedTargetRuleAllowedDep) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} should only have deps " +
                                "with ${targetRule.with}"
                    )
                }
            }

        configuration.rules.noRelationship.firstOrNull { rule -> rule.target == targetModule.moduleName }
            ?.run {
                if (sortedInternalProjectDependencies.isNotEmpty()) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} should not have deps"
                    )
                }
            }

        configuration.rules.doNotWith.firstOrNull { rule -> rule.target == targetModule.moduleName }
            ?.let { targetRule ->
                val isThereNotAllowedDep = sortedInternalProjectDependencies.any { internalDep ->
                    targetRule.notWith.contains(internalDep)
                }
                if (isThereNotAllowedDep) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} has a not allowed dependency"
                    )
                }
            }
        return CheckResult.Success
    }
}