package io.github.gabrielbmoro.popcorn.domain

import io.github.gabrielbmoro.popcorn.domain.entity.CheckResult
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornDoNotWithRule
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornJustWithRule
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornNoRelationShipRule
import io.github.gabrielbmoro.popcorn.domain.entity.TargetModule

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