package com.gabrielbmoro.popcorn.domain.usecase

import com.gabrielbmoro.popcorn.domain.entity.ArcViolationRule
import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.TargetModule

class CheckArchitectureUseCase {

    fun execute(rules: List<ArcViolationRule>, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies.sorted()

        val targetRule = rules.firstOrNull { rule ->
            rule.targetModule == targetModule.moduleName
                    || targetModule.isFeatureModule && rule is ArcViolationRule.Feature
        } ?: return CheckResult.Success

        when (targetRule) {
            is ArcViolationRule.JustWith -> {
                if (sortedInternalProjectDependencies != targetRule.justWith) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} should only have deps " +
                                "with ${targetRule.justWith}"
                    )
                }
            }

            is ArcViolationRule.NoRelationship -> {
                if (sortedInternalProjectDependencies.isNotEmpty()) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} should not have deps"
                    )
                }
            }

            is ArcViolationRule.Feature -> {
                if (sortedInternalProjectDependencies.contains("data")) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} is a feature module and " +
                                "should not have dependency with data"
                    )
                }
            }
        }

        return CheckResult.Success
    }
}