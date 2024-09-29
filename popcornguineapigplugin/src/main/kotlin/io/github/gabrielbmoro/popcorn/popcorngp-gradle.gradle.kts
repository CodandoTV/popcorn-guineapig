package io.github.gabrielbmoro.popcorn

import io.github.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import io.github.gabrielbmoro.popcorn.presentation.tasks.PopcornTask
import kotlinx.serialization.json.Json
import org.gradle.kotlin.dsl.register

val configurationPath = project.rootDir.path
    .plus(File.separator)
    .plus("config")
    .plus(File.separator)
    .plus("popcorn.json")

val configurationFile = file(configurationPath)

val configurationText = StringBuilder()
configurationFile.useLines { lines ->
    val lineIterator = lines.iterator()
    while (lineIterator.hasNext()) {
        configurationText.append(lineIterator.next())
    }
}

val configureTextContent = configurationText.toString()

val popcornConfiguration = Json.decodeFromString<PopcornConfiguration>(configureTextContent)

println("Popcorn config: $popcornConfiguration")

tasks.register<PopcornTask>("popcorn") {
    configuration = popcornConfiguration
}