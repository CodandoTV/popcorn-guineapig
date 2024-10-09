package com.gabrielbmoro.popcorn.presentation.ext

import com.gabrielbmoro.popcorn.domain.output.ArchitectureViolationError

fun List<ArchitectureViolationError>.toErrorMessage(): String? {
    return runCatching {
        map { it.toString() }
            .reduce { acc, s -> "$acc\n$s" }
    }.getOrNull()
}
