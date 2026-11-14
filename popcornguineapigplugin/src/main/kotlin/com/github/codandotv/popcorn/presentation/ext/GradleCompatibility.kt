package com.github.codandotv.popcorn.presentation.ext

import org.gradle.api.GradleException
import org.gradle.util.GradleVersion

internal const val MINIMUM_GRADLE_VERSION = "8.11"

internal fun requireMinimumGradleVersion(current: GradleVersion = GradleVersion.current()) {
    if (current < GradleVersion.version(MINIMUM_GRADLE_VERSION)) {
        throw GradleException(
            "PopcornGP requires Gradle $MINIMUM_GRADLE_VERSION or newer. " +
                    "The current Gradle version is ${current.version}."
        )
    }
}
