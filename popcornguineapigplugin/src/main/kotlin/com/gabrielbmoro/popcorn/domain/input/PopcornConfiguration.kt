package com.gabrielbmoro.popcorn.domain.input

import com.gabrielbmoro.popcorn.domain.rules.PopcornGuineaPigRule

data class PopcornConfiguration(
    val project: PopcornProject,
    val rules: List<PopcornGuineaPigRule>
)
