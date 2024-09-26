[![Kotlin](https://img.shields.io/badge/kotlin-1.9.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/CodandoTV/popcorn-guineapig/issues)

# Welcome! 👋

Welcome to the **Popcorn Gradle Plugin**!

This project is currently under development 🛠️

The goal of this plugin is to help enforce architectural rules in your project. Once you apply the plugin and specify your architecture guidelines in a `config/popcorn.json` file (located at the root of your project), the plugin will automatically verify whether your architecture adheres to these rules.

## Installation

Currently, this plugin is not deployed, so this section will be updated soon.

## How to use?

1. Specify a `config/popcorn.json` file in the root part of your project:

```json
{
  "project": {
    "type": "java"
  },
  "rules": {
    "noRelationship": [
      {
        "target": "platform"
      }
    ],
    "justWith": [],
    "doNotWith": [
      {
        "target": "[a-z]+-presentation",
        "notWith": [
          "[a-z]+-data"
        ]
      }
    ]
  }
}
```

The project type can be `java`, `kmp`, or `android`.

The rules are predefined, so we have a limited set of rules for now.

2. Apply the plugin to your module:

```kotlin
plugins {
    id("popcorn-guineapig-plugin")
    ...
}
...
```

3. Check if there is some architecture violation in your module:

```shell
./gradlew :sample:sample-presentation:popcorn
```

That's it! 🍿🐹

