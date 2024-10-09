package com.gabrielbmoro.popcorn.domain.input

data class PopcornProject(
    val type: ProjectType,
    val groupName: String? = null
)
