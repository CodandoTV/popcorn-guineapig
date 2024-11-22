package com.github.codandotv.popcorn.domain.output

internal sealed class CheckResult {
    object Success : CheckResult()

    data class Failure(
        val errors: List<ArchitectureViolationError>
    ) : CheckResult()
}
