package com.gabrielbmoro.popcorn.domain.entity

data class ArchitectureViolationError(
    val targetModule: TargetModule,
    val rule: BaseArcRule,
) {
    override fun toString(): String {
        return "$targetModule ::: ${rule.message()}"
    }
}