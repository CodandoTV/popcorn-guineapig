package com.github.codandotv.popcorn.domain.models

internal data class ModuleMetric(
    val name: String,
    val fanIn: Int,
    val fanOut: Int,
    val instability: Float
)
