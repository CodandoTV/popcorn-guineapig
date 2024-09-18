package com.gabrielbmoro.popcorn.domain.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProjectType(val configurationTarget: String) {
    @SerialName("java")
    JAVA("implementation"),
    @SerialName("kmp")
    KMP("commonImplementation"),
    @SerialName("android")
    ANDROID("implementation")
}
