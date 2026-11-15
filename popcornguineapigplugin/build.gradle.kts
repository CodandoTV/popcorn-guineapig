import java.util.Properties

plugins {
    `kotlin-dsl`
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.kover)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven(url = "https://plugins.gradle.org/m2/")
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
}

tasks.test {
    testLogging {
        events("passed", "skipped", "failed")
    }
}

val versionPropertiesFile = file("../popcornguineapigplugin/version.properties")
val versionProperties = Properties().apply {
    load(versionPropertiesFile.inputStream())
}

val versionPublish = versionProperties.getProperty("VERSION")

gradlePlugin {
    plugins {
        create("popcorngpparent") {
            id = "io.github.codandotv.popcorngpparent"
            implementationClass = "com.github.codandotv.popcorn.presentation.PopcornGpParentPlugin"
            version =  versionPublish
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    coordinates(
        project.property("GROUP_ID") as String,
        project.property("ARTIFACT_ID") as String,
        versionPublish
    )

    pom {
        name.set(project.property("ARTIFACT_ID") as String)
        description.set(project.property("ARTIFACT_ID") as String)
        inceptionYear.set("2024")
        url.set(project.property("POM_URL") as String)

        licenses {
            license {
                name.set(project.property("POM_LICENSE_NAME") as String)
                url.set(project.property("POM_LICENSE_URL") as String)
            }
        }
        scm {
            connection.set("scm:git@github.com:CodandoTV/popcorn-guineapig.git")
            url.set("https://github.com/CodandoTV/popcorn-guineapig.git")
        }
        developers {
            developer {
                id.set(project.property("POM_DEVELOPER_ID") as String)
                name.set(project.property("POM_DEVELOPER_NAME") as String)
                email.set(project.property("POM_DEVELOPER_EMAIL") as String)
            }
        }
    }
}

kotlin {
    explicitApi()
}

tasks.koverHtmlReport {
    dependsOn("test") // Ensure tests are run before generating the report
}
