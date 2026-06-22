# Getting started 🚀

## Minimum requirements

| Requirement | Minimum version |
|-------------|----------------|
| Java | 17 |
| Gradle | 8.7 |
| Kotlin | 2.2.0 |
| Android Gradle Plugin | 8.7.3 (for Android projects) |

## 1. Add the Plugin Dependency

Go to your build-logic folder, in the `build-logic/build.gradle.kts`, add the following dependency:

```kotlin
implementation("io.github.codandotv:popcornguineapig:<version>")
```

## 2. Apply the Plugin

You can use the parent plugin to define all your rules in a centralized place.

### 2.1. Parent plugin

Starting with version v3.1.0, we introduced support for a parent plugin, enabling developers to define all project rules in a centralized location.

Let's create a plugin that will be applied to the root build.gradle file of our project:

```kotlin
// build-logic/popcorngp-setup-plugin.gradle.kts
plugins {
  ...
  id("io.github.codandotv.popcorngpparent")
}
```

#### Configure All Your Architecture Rules

Now, we can specify in a single place all the rules that we want to apply to our project.

```kotlin
// build-logic/popcorngp-setup-plugin.gradle.kts
popcornGuineapigParentConfig {
    type = ProjectType.KMP

    children = listOf(
        PopcornChildConfiguration(
            moduleNameRegex = ":util:[a-z]+",
            rules = listOf(
                NoDependencyRule(),
            ),
        ),
        PopcornChildConfiguration(
            moduleNameRegex = ":feature:[a-z]+",
            rules = listOf(
                DoNotWithRule(
                    notWith = listOf("data"),
                ),
            ),
        ),
        // ... more children
    )
}
```

Don't forget to apply the plugin to the root build.gradle file:

```kotlin
// root/build.gradle.kts
plugins {
    id("popcorngp-setup-plugin")
    ...
}
```

#### **Run the task**

```sh
./gradlew popcornParent
```

!!!warning "For Gradle projects with Cache enabled for the  Configuration Phase"
    In case your project has cache enabled for the Gradle configuration phase, you need to disable that for popcorngp. So your command should be:
    ```sh
    ./gradlew popcornParent --no-configuration-cache
    ```

### 2.2. AI-Assisted Setup 🤖

!!!note "AI skill available >= 3.2.3"
    Starting from version `3.2.3`, PopcornGP ships with an embedded AI skill file that helps AI coding tools configure the plugin automatically.

To install the skill file in your project, run:

```sh
./gradlew installPopcornSkill
```

This creates a `SKILL.md` file that AI tools (OpenCode, Claude Code, Cursor) can use. See the [AI Skill Installation](./8-ai-skill-installation.md) page for custom destinations and details.

---

It is simple as a popcorn 🍿 + 🐹

Any problems you are facing, any suggestions you want to add, please feel free to [reach us out](mailto:gabrielbronzattimoro.es@gmail.com).