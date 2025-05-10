package com.github.codandotv.popcorn.domain.input

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

data class PopcornChildConfiguration(
    val moduleNameRegex: String,
    val rules: List<PopcornGuineaPigRule>,
)
