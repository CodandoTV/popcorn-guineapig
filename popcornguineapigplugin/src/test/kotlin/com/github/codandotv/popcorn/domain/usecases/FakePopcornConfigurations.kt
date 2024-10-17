package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.domain.input.PopcornProject
import com.github.codandotv.popcorn.domain.input.ProjectType

val fakePopcornConfiguration = PopcornConfiguration(
    project = PopcornProject(
        type = ProjectType.KMP
    ),
    rules = emptyList()
)
