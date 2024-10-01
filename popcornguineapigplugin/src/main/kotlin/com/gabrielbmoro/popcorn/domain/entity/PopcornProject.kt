package com.gabrielbmoro.popcorn.domain.entity

data class PopcornProject(
    val type: ProjectType,
    val groupName: String? = null
)
