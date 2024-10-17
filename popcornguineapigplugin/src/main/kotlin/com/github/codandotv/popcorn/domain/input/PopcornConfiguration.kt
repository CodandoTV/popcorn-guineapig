package com.github.codandotv.popcorn.domain.input

import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule

data class PopcornConfiguration(
    val project: PopcornProject,
    val rules: List<PopcornGuineaPigRule>
)
