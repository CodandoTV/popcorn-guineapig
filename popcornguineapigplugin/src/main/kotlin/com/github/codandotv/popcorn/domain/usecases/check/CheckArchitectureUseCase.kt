package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.identityKey

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
            .distinctBy { it.identityKey }
            .sortedWith(
                compareBy(
                    { it.moduleName },
                    { it.modulePath ?: "" },
                )
            )

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
