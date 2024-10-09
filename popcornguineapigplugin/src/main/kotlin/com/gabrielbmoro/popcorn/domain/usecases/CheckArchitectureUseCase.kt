package com.gabrielbmoro.popcorn.domain.usecases

import com.gabrielbmoro.popcorn.domain.output.ArchitectureViolationError
import com.gabrielbmoro.popcorn.domain.output.CheckResult
import com.gabrielbmoro.popcorn.domain.metadata.TargetModule
import com.gabrielbmoro.popcorn.domain.input.PopcornConfiguration

internal class CheckArchitectureUseCase {

    fun execute(configuration: PopcornConfiguration, targetModule: TargetModule): CheckResult {
        val sortedInternalProjectDependencies = targetModule.internalDependencies
            .distinct()
            .sortedBy {
                it.moduleName
            }

        val errors = mutableListOf<ArchitectureViolationError>()

        configuration.rules.forEach { rule ->
            rule.check(sortedInternalProjectDependencies)?.let { error ->
                errors.add(error)
            }
        }

        return if (errors.isEmpty()) {
            CheckResult.Success
        } else {
            CheckResult.Failure(
                errors = errors
            )
        }
    }
}
