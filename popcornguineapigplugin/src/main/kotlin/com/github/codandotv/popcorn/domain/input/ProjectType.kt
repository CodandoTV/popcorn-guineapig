package com.github.codandotv.popcorn.domain.input

enum class ProjectType {
    JAVA,
    KMP,
    ANDROID
}

fun ProjectType.configurationName() = when (this) {
    ProjectType.KMP -> "commonMainImplementation"
    ProjectType.JAVA, ProjectType.ANDROID -> "implementation"
}
