package com.gabrielbmoro.popcorn.domain.usecases

import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.entity.PopcornProject
import com.gabrielbmoro.popcorn.domain.entity.PopcornRules
import com.gabrielbmoro.popcorn.domain.entity.ProjectType

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
