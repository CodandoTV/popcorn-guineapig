package com.github.codandotv.popcorn.domain.input

public enum class ProjectType {
    JAVA,
    KMP,
    ANDROID
}

internal fun ProjectType.configurationNames() = when (this) {
    ProjectType.KMP -> listOf(
        "commonMainImplementation",
        "androidMainImplementation",
        "iosMainImplementation"
    )

    ProjectType.JAVA, ProjectType.ANDROID -> listOf(
        "implementation"
    )
}
