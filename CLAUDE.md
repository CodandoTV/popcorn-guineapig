# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Popcorn Gradle Plugin is a Gradle plugin designed to enforce architectural rules in multi-module projects. It validates dependencies between modules based on configurable architectural rules, supporting Kotlin Multiplatform Projects (KMP), Java Projects, and Android Projects.

## Build System & Commands

This is a **Gradle-based Kotlin project** with a multi-module structure:

### Core Commands

- **Run plugin tests**: `./gradlew popcornguineapigplugin:koverHtmlReport`
  - Executes all unit tests and generates code coverage report
  - Coverage report generated in `popcornguineapigplugin/build/reports/kover/html/`

- **Run single test**: `./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest"`
  - Pattern: `./gradlew popcornguineapigplugin:test --tests "path.to.TestClass"`

- **Build plugin locally**: `./gradlew popcornguineapigplugin:build`

- **Publish to Maven Central**: `./gradlew popcornguineapigplugin:publish --no-daemon`
  - Requires credentials and proper version in `version.properties`

- **Clean build**: `./gradlew clean`

### Build Configuration

- **Java version**: JDK 17 (set in GitHub Actions and required for builds)
- **Gradle version**: Managed via `gradlew` wrapper
- **Key properties** in `gradle.properties`:
  - `org.gradle.jvmargs=-Xmx4608m` (memory allocation)
  - Kotlin Compiler Daemon disabled due to KT-65761

## Project Architecture

### Structure

```
popcornguineapigplugin/
├── src/main/kotlin/com/github/codandotv/popcorn/
│   ├── presentation/      # Gradle plugin entry point & task definitions
│   ├── domain/            # Core business logic & rule implementations
│   ├── data/              # Repository implementation & report formatting
│   └── ServiceLocator.kt  # Service locator for dependency injection
├── src/test/kotlin/       # Unit tests for all layers
└── build.gradle.kts       # Plugin build configuration
```

### Core Layers

1. **Presentation Layer** (`presentation/`)
   - `PopcornGpParentPlugin` - Main plugin entry point
   - Task implementations that invoke domain use cases
   - Handles Gradle integration and reporting

2. **Domain Layer** (`domain/`)
   - **Rule Types**: `NoDependencyRule`, `JustWithRule`, `DoNotWithRule` - Enforce architectural constraints
   - **Use Cases**: `CheckArchitectureUseCase`, `GenerateReportUseCase` - Core plugin logic
   - **Models**: `ProjectType` (KMP, Java, Android), module graph representations
   - **Report models**: `ReportData`, `AnalysisTableItemData`, `AnalysisTableResultEnumData`, `ReportInfo` - pure domain data classes (moved from `data/dto/`)

3. **Data Layer** (`data/`)
   - `PopcornGuineapigRepositoryImpl` - Loads project modules and dependencies
   - `ReportDataSource` / `ReportDataExt` - Formats violations into markdown/table format

### Key Entry Points

- **Plugin registration**: `PopcornGpParentPlugin.apply()` - Registers `popcornParent` task
- **Main task**: `popcornParent` - Runs architecture validation
- **Configuration**: Via `popcorn { }` DSL block in build.gradle files
- **Service locator**: `ServiceLocator` - Wires repository and use cases together (replaces `DependencyFactory`)

## Testing

- **Test framework**: JUnit with Kotlin Test
- **Coverage tool**: Kover
- **Test location**: Tests mirror source structure under `src/test/kotlin/`
- **Fakes**: `FakePopcornGuineapigRepository` used for testing without real Gradle projects
- **Example tests**:
  - Rule validation: `NoDependencyRuleTest`, `JustWithRuleTest`, `DoNotWithRuleTest`
  - Report generation: `ReportDataToMarkDownFormatTest`, `ReportDataToMarkDownTableLineTest`, `GenerateReportUseCaseTest`

## Dependencies & Versions

Managed through `gradle/libs.versions.toml` (TOML catalog):
- **Kotlin**: 2.0.21 (JVM target, explicit API mode enabled)
- **Gradle API**: Latest compatible version
- **Kotlin Serialization**: For data model serialization
- **Vanniktech Maven Publish**: Handles Maven Central publishing

## CI/CD

- **GitHub Actions Workflow** (`pr.yml`):
  - Runs on pull requests to `main` branch
  - Executes: `./gradlew popcornguineapigplugin:koverHtmlReport`
  - Requires JDK 17
  - Uses Gradle caching for performance

- **Publish Workflow** (`publish.yml`):
  - Publishes to Maven Central when triggered
  - Command: `./gradlew popcornguineapigplugin:publish --no-daemon`

## Version Management

- Version stored in `popcornguineapigplugin/version.properties`
- Read during build and applied to published artifact
- Follow semantic versioning for releases

## Documentation

- Main docs: `docs/` directory with MkDocs setup
- Publishing guide: `docs/1-getting-started.md`
- Rule definitions: `docs/2-existing-rules.md`
- Custom rules: `docs/3-custom-rules.md`
- Error handling: `docs/4-error-report.md`

## Key Files & Their Purposes

- `settings.gradle.kts` - Enables type-safe project accessors, includes plugin build
- `build.gradle.kts` - Root build setup with shared plugins
- `popcornguineapigplugin/build.gradle.kts` - Plugin-specific build with Maven publishing config
- `.mega-linter.yml` - Code quality checks (linting configuration)
