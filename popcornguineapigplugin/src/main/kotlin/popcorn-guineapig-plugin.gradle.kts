import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.presentation.tasks.PopcornTask
import kotlinx.serialization.json.Json

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