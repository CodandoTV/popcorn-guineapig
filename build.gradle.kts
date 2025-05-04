@file:Suppress("UnstableApiUsage")

import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule


plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover) apply false
    id("io.github.codandotv.popcorngp.parent")
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
}

/*popcornGuineapigConfig {
    skippedRules = listOf(NoDependencyRule::class)
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.JAVA
        ),
        rules = listOf(
            NoDependencyRule(),
            DoNotWithRule(
                notWith = listOf("[a-z]+-data")
            )
        )
    )
}
*/