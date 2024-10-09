package com.gabrielbmoro.popcorn.domain.usecases

import com.gabrielbmoro.popcorn.domain.input.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.input.PopcornProject
import com.gabrielbmoro.popcorn.domain.input.ProjectType

val fakePopcornConfiguration = PopcornConfiguration(
    project = PopcornProject(
        type = ProjectType.KMP
    ),
    rules = emptyList()
)
