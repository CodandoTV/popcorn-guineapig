package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

interface CheckArchitectureUseCase {
    fun execute(
        internalDependencies: List<InternalDependenciesMetadata>,
        rules: List<PopcornGuineaPigRule>
    ): CheckResult
}

internal class CheckArchitectureUseCaseImpl : CheckArchitectureUseCase {

    override fun execute(
        internalDependencies: List<InternalDependenciesMetadata>,
        rules: List<PopcornGuineaPigRule>
    ): CheckResult {
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
