package io.github.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class PopcornProject(
    val type: ProjectType,
)
