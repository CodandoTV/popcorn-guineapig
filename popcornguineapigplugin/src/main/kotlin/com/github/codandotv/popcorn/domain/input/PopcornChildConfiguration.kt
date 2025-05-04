package com.github.codandotv.popcorn.domain.input

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule
import kotlin.reflect.KClass

data class PopcornChildConfiguration(
    val moduleNameRegex: String,
    val rules: List<PopcornGuineaPigRule>,
    val skippedRules: List<KClass<*>>?,
)