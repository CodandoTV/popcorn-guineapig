package io.github.gabrielbmoro.popcorn.domain.usecases

import io.github.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornProject
import io.github.gabrielbmoro.popcorn.domain.entity.PopcornRules
import io.github.gabrielbmoro.popcorn.domain.entity.ProjectType

val fakePopcornConfiguration = PopcornConfiguration(
    project = PopcornProject(
        type = ProjectType.KMP
    ),
    rules = PopcornRules(
        noRelationship = emptyList(),
        justWith = emptyList(),
        doNotWith = emptyList()
    )
)
