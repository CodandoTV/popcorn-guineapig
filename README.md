[![Kotlin](https://img.shields.io/badge/kotlin-1.9.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.codandotv/popcornguineapig)](https://central.sonatype.com/artifact/io.github.codandotv/popcornguineapig)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/CodandoTV/popcorn-guineapig/issues)

# Welcome! 👋

Welcome to the **Popcorn Gradle Plugin**!

<img height="150px" width="100px" src="img/popcorngp-logo.webp" />

The goal of this plugin is to help enforce architectural rules in your project. Once you apply the plugin and specify your architecture guidelines, the plugin will automatically verify whether your architecture adheres to these rules.

📚 Take a look at our [documentation](https://codandotv.github.io/popcorn-guineapig)

## 🚀 Getting Started

## 1. Add the Plugin Dependency

Go to your build-logic folder, in the `build-logic/build.gradle.kts`, add the following dependency:

```
implementation("io.github.codandotv:popcornguineapig:<version>")
```

### 2. Apply the Plugin

You can chose a conventional gradle plugin to define your rules. 

For example, I have a gradle plugin applied to all modules `kmp-library-setup.gradle.kts`. In this conventional plugin, you can add:


```kotlin
plugins {
  ...
  id("io.github.codandotv.popcorngp")
}
```

### 3. Configure Your Architecture Rules

After apply the plugin, you can sync and define the architecture rules:

```kotlin
popcornGuineapigConfig {
    // You also can skip rules to help duing migration
    skippedRules = listOf(DoNotWithRule::class)
    
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.JAVA
        ),
        rules = listOf(
            NoDependencyRule(),
            DoNotWithRule(
                notWith = listOf("[a-z]+-data")
            )
        )
    )
}
```

You also can create custom rules, you just need to do:

```kotlin
class MyRule : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        return null
    }
}

popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.JAVA
        ),
        rules = listOf(
            MyRule(),
        )
    )
}
```

### 4. **Run the task**

```sh
./gradlew popcorn
```

It is simple as a popcorn 🍿 + 🐹

## 🎯 Supported Project Types

The Popcorn Gradle Plugin supports:

- Kotlin Multiplatform Projects (KMP)

- Java Projects

- Android Projects
