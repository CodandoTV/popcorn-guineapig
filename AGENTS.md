# Popcorn GuineaPig — Project Reference

## Purpose

Popcorn GuineaPig is a Gradle plugin that enforces architectural rules in multi-module projects. It validates module dependency graphs against user-defined rules (NoDependency, JustWith, DoNotWith) and generates error/metrics reports.

## Language & Platform

- **Language**: Kotlin 2.2.0, JVM target
- **Explicit API mode**: enabled
- **Minimum Java**: JDK 17 (required locally and in CI)

## Build System

- **Gradle**: wrapper-managed; version catalog at `gradle/libs.versions.toml`
- **Dependencies**: centralized in TOML catalog — Kotlin Serialization, Vanniktech Maven Publish, Kover
- **Version file**: `popcornguineapigplugin/version.properties` (current: `3.2.1`)
- **JVM args**: `-Xmx4608m`
- **Kotlin Compiler Daemon**: intentionally disabled (KT-65761)

## Modules

Single Gradle module: `popcornguineapigplugin` — contains the plugin source, tests, and publishing config.

**Source structure** (`src/main/kotlin/com/github/codandotv/popcorn/`):

| Layer | Path | Responsibility |
|-------|------|---------------|
| `presentation/` | `PopcornGpParentPlugin.kt`, `tasks/` | Gradle integration, task registration |
| `domain/` | `rules/`, `usecases/`, `models/`, `input/` | Pure business logic (no Gradle imports) |
| `data/` | `PopcornGuineapigRepositoryImpl.kt`, `report/` | I/O, file writing, formatting |

## Plugin

- **Plugin ID**: `io.github.codandotv.popcorngpparent`
- **Entry class**: `com.github.codandotv.popcorn.presentation.PopcornGpParentPlugin`
- **Task**: `popcornParent` (runs architecture validation)
- **Config DSL**: `popcorn { }` block in build scripts
- **Maven Central**: `io.github.codandotv:popcornguineapig:<version>`
- **License**: MIT

## Testing

- **Framework**: JUnit 4 + Kotlin Test
- **Coverage**: Kover (runs via `koverHtmlReport`)
- **Fakes**: `FakePopcornGuineapigRepository`, `FakeLogger` in `src/test/kotlin/fakes/`
- **Test command**: `./gradlew popcornguineapigplugin:test`

## Documentation

- **Tool**: MkDocs with Material theme
- **Config**: `mkdocs.yml`
- **Source**: `docs/` directory
- **Hosted**: GitHub Pages at `https://codandotv.github.io/popcorn-guineapig`

## Versioning

- **Semantic versioning** (MAJOR.MINOR.PATCH)
- **Version stored**: `popcornguineapigplugin/version.properties`
- **Git tags**: 24 tags from v1.0.0 to v3.1.6

## CI/CD

| Workflow | Trigger | Action |
|----------|---------|--------|
| `pr.yml` | PR to `main` | `koverHtmlReport` on JDK 17 |
| `publish.yml` | Manual (`workflow_dispatch`) | Publish to Maven Central (Vanniktech + Fastlane) |
| `documentation.yml` | PR to `main` with docs changes | Deploy MkDocs to GitHub Pages |
| `mega-linter.yml` | PR to `main` | MegaLinter Java flavor |

## Development Scripts

- `scripts/publish-local.sh` — publish to `mavenLocal` for local testing
- `scripts/detektcheck.sh` — run Detekt static analysis

## Git Workflow

- **Branch convention**: `feature/*`, `gmoro/*`, `improvement/*`
- **Merge**: squash merge via PR
- **Commit messages**: clear, descriptive prefixes (`Bump`, `Update`, `Add`, `Fix`)
