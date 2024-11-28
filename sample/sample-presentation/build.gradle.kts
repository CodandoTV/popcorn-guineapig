
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.domain.input.PopcornProject
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("io.github.codandotv.popcorngp")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(projects.sample.sampleData)
}

popcornGuineapigConfig {
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
    hasReportEnabled = true
}
