package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError

internal fun List<ArchitectureViolationError>.toErrorMessage(): String? {
    return runCatching {
        map { it.toString() }
            .reduce { acc, s -> "$acc\n$s" }
    }.getOrNull()
}
