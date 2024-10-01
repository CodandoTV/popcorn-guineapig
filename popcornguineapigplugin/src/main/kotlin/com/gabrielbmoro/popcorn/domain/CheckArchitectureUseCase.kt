package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.*

class CheckArchitectureUseCase {

    fun execute(configuration: PopcornConfiguration, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies
            .distinct()
            .sortedBy {
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