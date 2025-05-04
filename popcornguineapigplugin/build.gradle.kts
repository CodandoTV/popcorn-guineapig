import com.vanniktech.maven.publish.SonatypeHost
import java.util.Properties

plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.vanniktech.maven.publish)
    alias(libs.plugins.kover)
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

val versionPropertiesFile = file("../popcornguineapigplugin/version.properties")
val versionProperties = Properties().apply {
    load(versionPropertiesFile.inputStream())
}

val versionPublish = versionProperties.getProperty("VERSION")

gradlePlugin {
    plugins {
        create("popcorngp") {
            id = "io.github.codandotv.popcorngp"
            implementationClass = "com.github.codandotv.popcorn.presentation.PopcornGpPlugin"
            version =  versionPublish
        }

        create("popcorngpparent") {
            id = "io.github.codandotv.popcorngpparent"
            implementationClass = "com.github.codandotv.popcorn.presentation.PopcornGpParentPlugin"
            version =  versionPublish
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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
            url.set("https://github.com/CodandoTV/CraftD.git")
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

tasks.koverHtmlReport {
    dependsOn("test") // Ensure tests are run before generating the report
}
