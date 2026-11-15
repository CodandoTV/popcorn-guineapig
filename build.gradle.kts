@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kover) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
