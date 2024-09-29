@file:Suppress("UnstableApiUsage")

include(
    ":sample:sample-main",
    ":sample:sample-presentation",
    ":sample:sample-domain",
    ":sample:sample-data"
)


enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("popcornguineapigplugin")
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
