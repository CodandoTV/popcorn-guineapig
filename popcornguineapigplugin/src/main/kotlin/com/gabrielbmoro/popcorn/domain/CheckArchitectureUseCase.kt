package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.PopcornDoNotWithRule
import com.gabrielbmoro.popcorn.domain.entity.PopcornJustWithRule
import com.gabrielbmoro.popcorn.domain.entity.PopcornNoRelationShipRule
import com.gabrielbmoro.popcorn.domain.entity.TargetModule

class CheckArchitectureUseCase {

    fun execute(configuration: PopcornConfiguration, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies.sortedBy {
            it.moduleName
        }

        val visitor = ArchitectureRuleVisitorImpl(
            targetModule = targetModule,
            sortedInternalProjectDependencies = sortedInternalProjectDependencies
        )

        runCatching {
            configuration.rules.all().filter { rule ->
                rule.target.toRegex().matches(targetModule.moduleName)
            }.forEach { targetRule ->
                when (targetRule) {
                    is PopcornJustWithRule -> visitor.doJustWithRule(targetRule)
                    is PopcornDoNotWithRule -> visitor.doForDoNotWithRule(targetRule)
                    is PopcornNoRelationShipRule -> visitor.doNoRelationshipRule(targetRule)
                }
            }
        }.onFailure {
            return CheckResult.Failure(
                errorMessage = it.message ?: ""
            )
        }

        return CheckResult.Success
    }
}