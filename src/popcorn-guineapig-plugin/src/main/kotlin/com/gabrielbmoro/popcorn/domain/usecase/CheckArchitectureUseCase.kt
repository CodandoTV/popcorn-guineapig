package com.gabrielbmoro.popcorn.domain.usecase

import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopCornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.TargetModule

class CheckArchitectureUseCase {

    fun execute(configuration: PopCornConfiguration, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies.sorted()

        configuration.rules.just_with.firstOrNull { rule -> rule.target == targetModule.moduleName }
            ?.let { targetRule ->
                if (sortedInternalProjectDependencies != targetRule.with) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} should only have deps " +
                                "with ${targetRule.with}"
                    )
                }
            }

        configuration.rules.no_relation_ship.firstOrNull { rule -> rule.target == targetModule.moduleName }
            ?.run {
                if (sortedInternalProjectDependencies.isNotEmpty()) {
                    return CheckResult.Failure(
                        errorMessage = "${targetModule.moduleName} should not have deps"
                    )
                }
            }
        return CheckResult.Success
    }
}