package com.gabrielbmoro.popcorn.domain.entity

internal sealed class CheckResult {
    object Success : CheckResult()

    data class Failure(
        val errors: List<ArchitectureViolationError>
    ) : CheckResult()
}
