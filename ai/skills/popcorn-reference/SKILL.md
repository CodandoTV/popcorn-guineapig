---
name: popcorn-reference
description: Use as a general reference for the Popcorn Gradle Plugin project — architecture rules, project structure, build configuration, CI/CD, and development workflows. Automatically loaded on session start.
---

# Popcorn Gradle Plugin — Project Reference

## Project Structure

```
popcornguineapigplugin/
├── src/main/kotlin/com/github/codandotv/popcorn/
│   ├── presentation/          # Gradle integration, tasks
│   │   ├── PopcornGpParentPlugin.kt
│   │   └── tasks/
│   ├── domain/               # Pure business logic
│   │   ├── rules/           # NoDependency, JustWith, DoNotWith
│   │   ├── usecases/        # CheckArchitecture, GenerateReport
│   │   ├── input/           # ProjectType, PopcornChildConfiguration
│   │   ├── models/          # TargetModule, InternalDependenciesMetadata
│   │   ├── PopcornGuineapigRepository.kt  # Interface contract
│   │   └── Logger.kt
│   ├── data/                # I/O and transformation
│   │   ├── report/          # Output formatting (ReportDataSource, etc.)
│   │   └── PopcornGuineapigRepositoryImpl.kt
│   └── ServiceLocator.kt    # Wires repository and use cases
│
├── src/test/kotlin/
│   ├── domain/              # Rule and use case tests
│   ├── data/                # DTO and formatting tests
│   ├── presentation/        # Gradle integration tests
│   └── fakes/               # FakeRepository for testing
│
├── build.gradle.kts
├── version.properties
└── src/

docs/                        # User documentation (MkDocs)
├── 1-getting-started.md
├── 2-existing-rules.md
├── 3-custom-rules.md
└── 4-error-report.md
```

## Build Configuration

- **Java**: JDK 17 (required locally and in CI)
- **Kotlin**: 2.2.0, JVM target, explicit API mode enabled
- **Gradle**: wrapper-managed; version catalog at `gradle/libs.versions.toml`
- **JVM args**: `-Xmx4608m` in `gradle.properties`
- **Kotlin Compiler Daemon**: intentionally disabled (KT-65761)
- **Dependencies**: centralized in TOML catalog
- **Version**: `popcornguineapigplugin/version.properties` (current: 3.2.1)

## Testing

- **Framework**: JUnit 4 + Kotlin Test
- **Coverage**: Kover (run via `koverHtmlReport`)
- **Fakes**: `FakePopcornGuineapigRepository`, `FakeLogger` in `src/test/kotlin/fakes/`
- **Test command**: `./gradlew popcornguineapigplugin:test`

## Common Workflows

### Implementing a new feature
1. Understand the architecture
2. Implement (Domain → Data → Presentation)
3. Write tests
4. Compile and test locally
5. Open PR and review

### Debugging a failing test
```bash
./gradlew popcornguineapigplugin:test
./gradlew popcornguineapigplugin:test --tests "FailingTestClass"
```

### Preparing for a release
1. Validate architecture
2. Run full tests
3. Compile
4. Update `popcornguineapigplugin/version.properties`
5. Merge and trigger `publish.yml` workflow

## FAQ

**Q: Where do I add a new rule?**
A: `domain/rules/MyNewRule.kt`. Implement `PopcornGuineaPigRule` with pure logic (no Gradle). Add tests in `src/test/kotlin/domain/rules/MyNewRuleTest.kt`.

**Q: How do I test a rule without loading a real Gradle project?**
A: Use `FakePopcornGuineapigRepository`. See `NoDependencyRuleTest.kt` as example.

**Q: Can I import Gradle in a rule (domain)?**
A: No. Domain is pure. Place Gradle API code in `data/` or `presentation/`.

**Q: Where do I add a new dependency?**
A: In `gradle/libs.versions.toml`. Never add hardcoded versions in `build.gradle.kts`.

## Before Committing

- [ ] `./gradlew popcornguineapigplugin:koverHtmlReport` passes (tests + coverage)
- [ ] `./gradlew popcornguineapigplugin:build` compiles without errors
- [ ] Architecture is correct (files in right layer, no Gradle in domain)
- [ ] Tests were added/updated
- [ ] Commit messages are clear
