[![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.codandotv/popcornguineapig)](https://central.sonatype.com/artifact/io.github.codandotv/popcornguineapig)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/CodandoTV/popcorn-guineapig/issues)

![Logo do CodandoTV](img/codandotv.png)

# Welcome! 👋

Welcome to the **Popcorn Gradle Plugin**! A CodandoTV library : )

<img height="150px" width="100px" src="img/popcorngp-logo.webp" />

The goal of this plugin is to help enforce architectural rules in your project. Once you apply the plugin and specify your architecture guidelines, the plugin will automatically verify whether your architecture adheres to these rules.

📚 Take a look at our [documentation](https://codandotv.github.io/popcorn-guineapig)

## 🚀 Getting Started

### 1. Add the Plugin Dependency

Go to your build-logic folder, in the `build-logic/build.gradle.kts`, add the following dependency:

```
implementation("io.github.codandotv:popcornguineapig:<version>")
```

### 2. Apply the Plugin and define your rules

Popcorn gives you the option to use a [parent plugin](https://codandotv.github.io/popcorn-guineapig/1-getting-started/#22-parent-plugin).

As for the rules, you can define things like: "I don't want modules with the word *presentation* 
in their name to depend on modules that have *data* in the name." This lets you use your module 
naming conventions to your advantage.

### 4. **Run the task**

```sh
./gradlew popcornParent
```

It is simple as a popcorn 🍿 + 🐹

## 🎯 Supported Project Types

The Popcorn Gradle Plugin supports:

- Kotlin Multiplatform Projects (KMP)

- Java Projects

- Android Projects
