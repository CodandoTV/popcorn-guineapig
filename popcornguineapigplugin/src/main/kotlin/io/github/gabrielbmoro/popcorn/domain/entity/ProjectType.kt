package io.github.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProjectType {
    @SerialName("java")
    JAVA,
    @SerialName("kmp")
    KMP,
    @SerialName("android")
    ANDROID
}
