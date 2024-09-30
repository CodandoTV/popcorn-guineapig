import com.gabrielbmoro.popcorn.domain.entity.*

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
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.JAVA
        ),
        rules = PopcornRules(
            noRelationship = emptyList(),
            justWith = emptyList(),
            doNotWith = listOf(
                PopcornDoNotWithRule(
                    target = "[a-z]+-presentation",
                    notWith = listOf(
                        "[a-z]+-data"
                    )
                )
            )
        )
    )
}