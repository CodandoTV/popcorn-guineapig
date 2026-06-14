---
name: minimum-requirements
description: Analyze project dependencies and update README with minimum system requirements for using the library.
---

# Minimum Requirements

Check all dependencies used by the project and determine the minimum requirements for using the library. Creates or updates a "Minimum requirements" section in README.md.

## When to use

- Before a release
- After updating dependencies
- When setting up CI/CD or documenting system prerequisites

## Steps

### 1. Read dependency catalog

Parse `gradle/libs.versions.toml` for all dependency versions:

```
[versions]
kotlin = "..."
junit = "..."
gradle = "..."
```

### 2. Determine minimum requirements

Find the minimum supported versions from source code and documentation:

- **Gradle** — check `gradle-wrapper.properties` and any `minGradleVersion` constants
- **Java** — check `gradle.properties` JVM args, CI workflow JDK setup, and `sourceCompatibility`/`targetCompatibility`
- **Kotlin** — check `kotlin` version in `libs.versions.toml` (the plugin requires the compiler version)
- **Android Gradle Plugin** — check `buildscript` dependencies if Android support is documented
- **Project types** — review `ProjectType.kt` (JAVA, KMP, ANDROID)

### 3. Read existing README

Read `README.md` and check whether a "Minimum requirements" section already exists.

### 4. Create or update the section

Write (or update) a "## Minimum requirements" section in `README.md` with a table like:

| Requirement | Minimum version |
|-------------|----------------|
| Java | 17 |
| Gradle | 8.x |
| Kotlin | 2.2.0 |
| Android Gradle Plugin | (if applicable) |

Insert the section before "## Getting Started".

### 5. Summary

Report what was found and what was written to README.
