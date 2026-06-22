---
name: setup-popcorn-plugin
description: Use when configuring a multi-module Kotlin Multiplatform or Android project to use the PopcornGpParentPlugin for architectural rule enforcement. Triggers on phrases like "setup popcorn", "configure popcorn plugin", "add popcorn to project", "architectural rules setup", "enforce module dependencies".
---

# Setup Popcorn Plugin

Guides the setup of `PopcornGpParentPlugin` in a multi-module Kotlin Multiplatform (KMP) or Android project to enforce architectural rules on module dependency graphs.

## When to use

- Initializing architectural rule enforcement in a new or existing multi-module project
- Migrating from per-module rule definitions to centralized parent plugin configuration
- Setting up CI/CD validation for module dependency boundaries
- Onboarding a team to automated architecture checks

## Prerequisites

| Requirement | Minimum version |
|-------------|----------------|
| Java | 17 |
| Gradle | 8.7 |
| Kotlin | 2.2.0 |
| Android Gradle Plugin | 8.7.3 (Android projects only) |

## Detection: choose the right setup path

Before configuring, determine how the project manages build infrastructure by checking these in order:

| Infrastructure detected | Where to place plugin + rules |
|-------------------------|-------------------------------|
| `build-logic/` directory exists | Convention plugin in `build-logic/`, applied in root |
| `buildSrc/` directory exists | Plugin class in `buildSrc/`, applied in root |
| Neither exists | Apply plugin + DSL directly in root `build.gradle.kts` |

Ask the user about their project structure if it is unclear.

## Path 1: build-logic (convention plugins)

### 1.1 Add dependency

In `build-logic/build.gradle.kts`, add:

```kotlin
implementation("io.github.codandotv:popcornguineapig:<latest-version>")
```

### 1.2 Create the setup convention plugin

Create `build-logic/src/main/kotlin/popcorngp-setup.gradle.kts`:

```kotlin
plugins {
    id("io.github.codandotv.popcorngpparent")
}

popcornGuineapigParentConfig {
    type = ProjectType.KMP  // or ProjectType.ANDROID

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
        // Add more children matching your module structure
    )
}
```

### 1.3 Apply the convention plugin

In root `build.gradle.kts`:

```kotlin
plugins {
    id("popcorngp-setup")
}
```

## Path 2: buildSrc

### 2.1 Add dependency

In `buildSrc/build.gradle.kts`, add:

```kotlin
implementation("io.github.codandotv:popcornguineapig:<latest-version>")
```

### 2.2 Create the plugin class

Create `buildSrc/src/main/kotlin/PopcornSetupPlugin.kt`:

```kotlin
import com.github.codandotv.popcorn.presentation.PopcornGpParentPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class PopcornSetupPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply(PopcornGpParentPlugin::class.java)

        target.extensions.configure("popcornGuineapigParentConfig") {
            // Use withGroovyBuilder or access via typed extension
        }
    }
}
```

Register it in `buildSrc/build.gradle.kts`:

```kotlin
gradlePlugin {
    plugins {
        create("popcornSetup") {
            id = "popcorn-setup"
            implementationClass = "PopcornSetupPlugin"
        }
    }
}
```

### 2.3 Apply in root

In root `build.gradle.kts`:

```kotlin
plugins {
    id("popcorn-setup")
}
```

## Path 3: Direct root build.gradle.kts

### 3.1 Apply plugin and configure

In root `build.gradle.kts`:

```kotlin
plugins {
    id("io.github.codandotv.popcorngpparent") version "<latest-version>"
}

popcornGuineapigParentConfig {
    type = ProjectType.KMP  // or ProjectType.ANDROID

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
    )
}
```

## Mapping module structure to rules

Apply clean architecture principles when writing regex patterns and rules:

| Architectural layer | Typical module pattern | Common rules |
|---------------------|----------------------|-------------|
| Utility / Common | `:util:*`, `:core:*`, `:common:*` | `NoDependencyRule()` — no internal dependencies |
| Domain | `:*:domain` | `NoDependencyRule()` or `DoNotWithRule(notWith = listOf("data", "presentation"))` |
| Data | `:*:data` | `JustWithRule(justWith = listOf("domain"))` — only depends on domain |
| Feature / UI | `:feature:*` | `DoNotWithRule(notWith = listOf("data"))` — depends on domain, not data directly |

### Guidelines

- Each `PopcornChildConfiguration` should represent a coherent set of modules with the same constraints
- Start minimal — add rules incrementally as the architecture stabilizes
- Prefer `DoNotWithRule` over `JustWithRule` when the allowed dependency list is unbounded
- Test regex patterns manually before committing: `":feature:home".matches(Regex(":feature:[a-z]+"))`
- All rule entries in `notWith` and `justWith` are treated as regular expressions

## Rule reference

| Rule | Constructor | Behavior |
|------|------------|----------|
| `NoDependencyRule` | `NoDependencyRule()` | Matched modules must have zero internal dependencies |
| `JustWithRule` | `JustWithRule(justWith = listOf("pattern"))` | Matched modules may only depend on modules matching at least one `justWith` pattern |
| `DoNotWithRule` | `DoNotWithRule(notWith = listOf("pattern"))` | Matched modules must NOT depend on modules matching any `notWith` pattern |

Custom rules can be created by implementing the `PopcornGuineaPigRule` interface.

## Project type selection

```kotlin
// For Kotlin Multiplatform projects
type = ProjectType.KMP
// Resolves dependencies from: commonMainImplementation, androidMainImplementation, iosMainImplementation

// For Android or plain JVM projects
type = ProjectType.ANDROID  // or ProjectType.JAVA
// Resolves dependencies from: implementation
```

## Verification

After setup, validate:

```shell
# Run the architecture check
./gradlew popcornParent

# Generate error report
./gradlew popcornParent -PerrorReportEnabled

# Generate module metrics
./gradlew popcornModuleMetrics

# If cached configuration phase is enabled
./gradlew popcornParent --no-configuration-cache
```

Error reports are placed at `<module>/build/reports/popcornguineapig/errorReport.md`.

## Red flags

- Configuring `JustWithRule` without clear module boundaries — the rule is strict and may break as modules grow
- Overly broad regex patterns (e.g. `:*`) that match unintended modules
- Forgetting to differentiate between KMP and Android `ProjectType` — wrong type means wrong dependency configurations are analyzed
- Not committing the convention plugin file — other developers won't get the same checks

## Clean code principles

- Match regex patterns to real architectural layers in the project, not arbitrary groupings
- Each module group should have a clear, single responsibility for its rules
- Keep the configuration file well-structured and commented with the intent behind each rule
- Review rules during PRs alongside code changes — architectural boundaries evolve with the project
