package com.github.codandotv.popcorn.domain.input

enum class ProjectType {
    JAVA,
    KMP,
    ANDROID
}

fun ProjectType.configurationNames() = when (this) {
    ProjectType.KMP -> listOf(
        "commonMainImplementation",
        "androidMainImplementation",
        "iosMainImplementation"
    )

    ProjectType.JAVA, ProjectType.ANDROID -> listOf(
        "implementation"
    )
}
