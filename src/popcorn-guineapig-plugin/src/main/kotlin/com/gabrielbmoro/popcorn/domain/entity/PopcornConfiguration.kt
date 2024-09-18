package com.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PopcornConfiguration(
    val project: PopcornProject,
    val rules: PopcornRules
)
