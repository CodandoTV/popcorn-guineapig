package com.github.codandotv.popcorn.domain.output

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

data class ArchitectureViolationError(
    val message: String,
    val rule: PopcornGuineaPigRule
) {
    override fun toString(): String {
        return message
    }
}
