package com.github.codandotv.popcorn.domain.input

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

public data class PopcornChildConfiguration(
    val moduleNameRegex: String,
    val rules: List<PopcornGuineaPigRule>,
)
