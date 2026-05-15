package com.github.codandotv.popcorn.domain.models

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError

internal sealed class CheckResult {
    object Success : CheckResult()

    data class Failure(
        val errors: List<ArchitectureViolationError>
    ) : CheckResult() {
        override fun toString(): String {
            return runCatching {
                errors.map { it.toString() }
                    .reduce { acc, s -> "$acc\n$s" }
            }.getOrNull() ?: ""
        }
    }
}
