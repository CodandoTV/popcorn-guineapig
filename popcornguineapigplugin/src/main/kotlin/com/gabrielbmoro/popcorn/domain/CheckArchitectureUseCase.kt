package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.*

internal class CheckArchitectureUseCase {

    fun execute(configuration: PopcornConfiguration, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies
            .distinct()
            .sortedBy {
                it.moduleName
            }

        val visitor = ArchitectureRuleVisitor(
            targetModule = targetModule,
            sortedInternalProjectDependencies = sortedInternalProjectDependencies
        )

        val errors = mutableListOf<ArchitectureViolationError>()

        configuration.rules
            .all()
            .filter { rule ->
                rule.target.toRegex().matches(targetModule.moduleName)
            }
            .forEach { targetRule ->
                val error: ArchitectureViolationError? = when (targetRule) {
                    is PopcornJustWithRule -> visitor.doJustWithRule(targetRule)
                    is PopcornDoNotWithRule -> visitor.doForDoNotWithRule(targetRule)
                    is PopcornNoRelationShipRule -> visitor.doNoRelationshipRule(targetRule)
                    else -> null
                }

                if (error != null) {
                    errors.add(error)
                }
            }

        if (errors.isEmpty()) {
            return CheckResult.Success
        } else {
            val errorMessage = errors
                .map { it.toString() }
                .reduce { acc, s -> "$acc\n$s" }

            return CheckResult.Failure(
                errorMessage = errorMessage
            )
        }
    }
}