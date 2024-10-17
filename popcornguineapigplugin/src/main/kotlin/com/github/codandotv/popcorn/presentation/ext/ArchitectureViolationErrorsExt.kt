package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError

fun List<com.github.codandotv.popcorn.domain.output.ArchitectureViolationError>.toErrorMessage(): String? {
    return runCatching {
        map { it.toString() }
            .reduce { acc, s -> "$acc\n$s" }
    }.getOrNull()
}
