plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("io.github.gabrielbmoro.popcorn.popcorngp-gradle")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(projects.sample.sampleData)
}