import java.util.Properties
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.provider.Provider

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

/**
 * Module ids of everything resolved for this configuration, walked from the root
 * of the resolution result so a transitive leak is caught as well as a direct one.
 */
fun ResolvedComponentResult.allModuleIds(): Set<String> {
    val visited = mutableSetOf<ResolvedComponentResult>()
    val ids = mutableSetOf<String>()

    fun collect(component: ResolvedComponentResult) {
        if (!visited.add(component)) return
        component.moduleVersion?.let { ids.add("${it.group}:${it.name}") }
        component.dependencies
            .filterIsInstance<ResolvedDependencyResult>()
            .forEach { collect(it.selected) }
    }

    collect(this)
    return ids
}

val runtimeComponentIds: Provider<List<String>> =
    configurations.runtimeClasspath
        .flatMap { it.incoming.resolutionResult.rootComponent }
        .map { it.allModuleIds().sorted() }

/**
 * AGP and KGP must stay off the published runtime classpath: this plugin only talks to
 * the Gradle API, and shipping either one forces a version on every consumer's
 * build-logic classpath, where it conflicts with their own (#112).
 */
val verifyNoRuntimeDependencyLeak by tasks.registering {
    group = "verification"
    description = "Fails when AGP or KGP leaks onto the plugin's runtime classpath."

    val componentIds = runtimeComponentIds

    doLast {
        val leaked = componentIds.get().filter { id ->
            id.startsWith("com.android.tools.build:") ||
                id.startsWith("org.jetbrains.kotlin:kotlin-gradle-plugin")
        }
        if (leaked.isNotEmpty()) {
            throw GradleException(
                "AGP/KGP leaked onto the plugin's runtime classpath: ${leaked.joinToString()}. " +
                    "Declare them as compileOnly and resolve them through pluginManager.withPlugin(...) instead.",
            )
        }
    }
}

tasks.named("check") {
    dependsOn(verifyNoRuntimeDependencyLeak)
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
