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
│   │   └── ...tasks
│   ├── domain/               # Pure business logic
│   │   ├── rules/           # NoDependency, JustWith, DoNotWith
│   │   ├── usecases/        # CheckArchitecture, GenerateReport
│   │   ├── input/           # ProjectType, input models
│   │   ├── metadata/        # TargetModule, InternalDependenciesMetadata
│   │   ├── output/          # CheckResult, ArchitectureViolationError
│   │   ├── report/          # ReportData, AnalysisTableItemData, ReportInfo
│   │   └── PopcornGuineapigRepository.kt  # Interface contract
│   ├── data/                # I/O and transformation
│   │   ├── report/          # Output formatting (ReportDataSource, ReportDataExt)
│   │   └── PopcornGuineapigRepositoryImpl.kt
│   └── ServiceLocator.kt    # Wires repository and use cases together
│
├── src/test/kotlin/         # Tests mirroring source structure
│   ├── domain/              # Rule and use case tests
│   ├── data/                # DTO and formatting tests
│   ├── presentation/        # Gradle integration tests
│   └── fakes/               # FakeRepository for testing without real Gradle projects
│
├── build.gradle.kts         # Plugin configuration
├── gradle/libs.versions.toml # Dependency versions (centralized)
├── gradle.properties        # JVM args, Kotlin settings
└── settings.gradle.kts      # Repositories, projects

docs/                        # User documentation
├── 1-getting-started.md
├── 2-existing-rules.md
├── 3-custom-rules.md
└── 4-error-report.md
```

## Key entry points

- **Plugin registration**: `PopcornGpParentPlugin.apply()` — registers `popcornParent` task
- **Main task**: `popcornParent` — runs architecture validation
- **Configuration**: via `popcorn { }` DSL block in build.gradle files

## Build Configuration

- **Java version**: JDK 17 (required locally and in CI)
- **Kotlin**: 2.0.21, JVM target, explicit API mode enabled
- **Gradle version**: managed via `gradlew` wrapper
- **Key `gradle.properties` settings**:
  - `org.gradle.jvmargs=-Xmx4608m`
  - Kotlin Compiler Daemon intentionally disabled (KT-65761)
- **Dependencies**: managed through `gradle/libs.versions.toml` (TOML catalog)
  - Kotlin Serialization — data model serialization
  - Vanniktech Maven Publish — Maven Central publishing

## CI/CD

- **PR workflow** (`pr.yml`): runs on PRs to `main`, executes `./gradlew popcornguineapigplugin:koverHtmlReport`, requires JDK 17, uses Gradle caching
- **Publish workflow** (`publish.yml`): publishes to Maven Central when triggered; requires configured credentials

## Version management

- Version stored in `popcornguineapigplugin/version.properties`
- Applied to published artifact at build time
- Follow semantic versioning (MAJOR.MINOR.PATCH)

## Common Workflows

### Implementing a new feature

1. Understand the architecture
2. Implement (Domain → Data → Presentation)
3. Write tests
4. Compile locally
5. Open PR and review

### Debugging a failing test

```bash
# 1. Run the full suite to identify failures
./gradlew popcornguineapigplugin:test

# 2. Run the specific test in isolation
./gradlew popcornguineapigplugin:test --tests "FailingTestClass"

# 3. Fix and re-run
```

### Preparing for a release

1. Validate architecture
2. Run full tests
3. Compile
4. Update `popcornguineapigplugin/version.properties`
5. Merge and trigger `publish.yml` workflow

## FAQ

**Q: Where do I add a new rule?**
A: `domain/rules/MyNewRule.kt`. Implement `ArchitectureRule` with pure logic (no Gradle). Add tests in `src/test/kotlin/domain/rules/MyNewRuleTest.kt`.

**Q: How do I test a rule without loading a real Gradle project?**
A: Use `FakePopcornGuineapigRepository`. See `NoDependencyRuleTest.kt` as example.

**Q: Can I import Gradle in a rule (domain)?**
A: No. Domain is pure. Place Gradle API code in `data/` or `presentation/`. Pure data models go in `domain/report/`; output formatting goes in `data/report/`.

**Q: Where do I add a new dependency?**
A: In `gradle/libs.versions.toml`. Never add hardcoded versions in `build.gradle.kts`.

**Q: How do I update the plugin version?**
A: In `popcornguineapigplugin/version.properties`. Follow semantic versioning (MAJOR.MINOR.PATCH).

**Q: Does the plugin publish automatically to Maven Central?**
A: Yes, via the `publish.yml` GitHub Actions workflow when triggered. Requires configured credentials.

## Before Committing

- [ ] `./gradlew popcornguineapigplugin:koverHtmlReport` passes (tests pass, coverage maintained)
- [ ] `./gradlew popcornguineapigplugin:build` compiles without errors
- [ ] Validate architecture confirms correct structure
- [ ] New files are in the right layer
- [ ] Tests were added/updated
- [ ] Commit messages are clear
