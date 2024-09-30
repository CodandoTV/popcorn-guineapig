import com.vanniktech.maven.publish.SonatypeHost

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.maven.publish)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradle)

    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val popcornGpVersion = "1.0.8"

gradlePlugin {
    plugins {
        create("popcorngp") {
            id = "io.github.gabrielbmoro.popcorngp"
            implementationClass = "com.gabrielbmoro.popcorn.PopcornGpPlugin"
            version = popcornGpVersion
        }
    }
}

mavenPublishing {
    signAllPublications()

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    coordinates(
        groupId = "io.github.gabrielbmoro",
        artifactId = "popcornguineapig",
        version = popcornGpVersion
    )

    pom {
        name.set("Popcornguineapig")
        description.set("Small plugin to check for architecture violation in a multi-module project")
        inceptionYear.set("2024")
        url.set("https://github.com/gabrielbmoro/popcorn-guineapig")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("gabrielbmoro")
                name.set("gabrielbronzattimoro.es@gmail.com")
                url.set("https://github.com/gabrielbmoro/")
            }
        }
        scm {
            url.set("https://github.com/gabrielbmoro")
            connection.set("scm:git:git://github.com/gabrielbmoro/popcorn-guineapig.git")
            developerConnection.set("scm:git:ssh://git@github.com/gabrielbmoro/popcorn-guineapig.git")
        }
    }
}
