@file:Suppress("UnstableApiUsage")

include(
    ":sample:sample-main",
    ":sample:sample-presentation",
    ":sample:sample-domain",
    ":sample:sample-data"
)


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("popcorn-guineapig-plugin")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PopcornGuineaPig"
