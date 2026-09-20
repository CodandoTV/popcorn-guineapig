---
name: build-and-check
description: Use when compiling the plugin or validating the build configuration. Triggers on phrases like "build plugin", "compile", "build check", "pre-push".
---

# Build & Check

Compiles the plugin and validates the build configuration.

## When to use
- After changes that affect the build
- To verify there are no compilation errors
- To prepare for a release

## Main commands

```bash
# Full build
./gradlew popcornguineapigplugin:build

# Clean build
./gradlew clean
./gradlew popcornguineapigplugin:build
```

## Common issues and solutions

| Issue | Solution |
|-------|----------|
| "Plugin is already compiled" | Run `./gradlew clean` first |
| "JVM memory error" | Increase `org.gradle.jvmargs` in `gradle.properties` |
| "Repository not accessible" | Check internet and URLs in `settings.gradle.kts` |
| Kotlin compiler daemon error | Expected — intentionally disabled (KT-65761) |

## Build output
- Plugin JAR: `popcornguineapigplugin/build/libs/popcornguineapigplugin-<version>.jar`
