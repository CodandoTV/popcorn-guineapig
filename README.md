[![Kotlin](https://img.shields.io/badge/kotlin-1.9.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.gabrielbmoro/popcornguineapig)](https://central.sonatype.com/artifact/io.github.gabrielbmoro/popcornguineapig)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/CodandoTV/popcorn-guineapig/issues)

# Welcome! 👋

Welcome to the **Popcorn Gradle Plugin**!

This project is currently under development 🛠️

The goal of this plugin is to help enforce architectural rules in your project. Once you apply the plugin and specify your architecture guidelines in a `config/popcorn.json` file (located at the root of your project), the plugin will automatically verify whether your architecture adheres to these rules.

## How to use? 🤔

Go to your build-logic folder, in the `build-logic/build.gradle.kts`, add the following dependency:

```
implementation("io.github.gabrielbmoro:popcornguineapig:1.0.8")
```

You can chose a conventional gradle plugin to define your rules. 

For example, I have a gradle plugin applied to all modules `kmp-library-setup.gradle.kts`. In this conventional plugin, you can add:

```
plugins {
  ...
  id("io.github.gabrielbmoro.popcorngp")
}

...
popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.KMP
        ),
        rules = PopcornRules(
            noRelationship = listOf(
                PopcornNoRelationShipRule("domain"),
                PopcornNoRelationShipRule("resources"),
                PopcornNoRelationShipRule("platform")
            ),
            justWith = listOf(
                PopcornJustWithRule(
                    target = "data",
                    with = listOf(
                        "domain"
                    )
                ),
                PopcornJustWithRule(
                    target = "designsystem",
                    with = listOf(
                        "resources"
                    )
                )
            ),
            doNotWith = emptyList()
        )
    )
}
```

After that, you can run:

```sh
./gradlew popcorn
```

It is simple as a popcorn 🍿 + 🐹