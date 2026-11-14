package com.github.codandotv.popcorn.domain.models

internal data class ModuleAnalysis(
    val modulePath: String,
    val result: CheckResult,
)
