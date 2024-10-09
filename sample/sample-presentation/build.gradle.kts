
import com.gabrielbmoro.popcorn.domain.input.PopcornConfiguration
import com.gabrielbmoro.popcorn.domain.input.PopcornProject
import com.gabrielbmoro.popcorn.domain.input.ProjectType
import com.gabrielbmoro.popcorn.domain.rules.DoNotWithRule
import com.gabrielbmoro.popcorn.domain.rules.NoDependencyRule

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("io.github.gabrielbmoro.popcorngp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(projects.sample.sampleData)
}

popcornGuineapigConfig {
    skippedRules = listOf(DoNotWithRule::class, NoDependencyRule::class)
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
