package com.gabrielbmoro.popcorn.domain.output

import com.gabrielbmoro.popcorn.domain.rules.PopcornGuineaPigRule

data class ArchitectureViolationError(
    val message: String,
    val rule: PopcornGuineaPigRule
) {
    override fun toString(): String {
        return message
    }
}
