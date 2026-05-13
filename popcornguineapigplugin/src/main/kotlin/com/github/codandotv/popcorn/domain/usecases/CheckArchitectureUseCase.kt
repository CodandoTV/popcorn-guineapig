package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError

internal interface CheckArchitectureUseCase {
    fun execute(
        targetModule: TargetModule
    ): CheckResult
}

internal class CheckArchitectureUseCaseImpl : CheckArchitectureUseCase {

    override fun execute(
        targetModule: TargetModule,
    ): CheckResult {
        val rules = targetModule.rules
        val internalDependencies = targetModule.internalDependencies
        val sortedInternalProjectDependencies = internalDependencies
            .distinct()
            .sortedBy {
                it.moduleName
            }

        val errors = mutableListOf<ArchitectureViolationError>()

        rules.forEach { rule ->
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
