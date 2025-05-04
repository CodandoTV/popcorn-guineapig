@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover) apply false
// 1. Example apply the parent plugin
//    id("io.github.codandotv.popcorngp.parent")
}

buildscript {
    repositories {
        mavenCentral()
        google()
    }

    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}


/*
// 1. Example apply the parent plugin
popcornGuineapigParentConfig {
    type = ProjectType.JAVA
    groupName = null
    children = listOf(
        PopcornChildConfiguration(
            rules = listOf(
                NoDependencyRule(),
                DoNotWithRule(
                    notWith = listOf("[a-z]+-data")
                )
            ),
            skippedRules = listOf(NoDependencyRule::class),
            moduleNameRegex = "[a-z]+-presentation"
        )
    )
}*/
