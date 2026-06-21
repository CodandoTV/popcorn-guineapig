# Popcorn GuineaPig — AI Context

A Gradle plugin that enforces architectural rules in multi-module projects.
Validates module dependency graphs against user-defined rules (NoDependency, JustWith, DoNotWith)
and generates error/metrics reports.

## Project Structure

```
popcorn-guineapig/                          # Root — Gradle aggregator
├── popcornguineapigplugin/                 # Plugin source (single module)
│   ├── src/main/kotlin/com/github/codandotv/popcorn/
│   │   ├── presentation/  (Gradle API, tasks, DSL)
│   │   ├── domain/        (Pure business logic — no Gradle imports)
│   │   └── data/          (I/O, file writing, formatting)
│   └── src/test/kotlin/
│       ├── domain/        (Rule and use case tests)
│       ├── data/          (DTO and formatting tests)
│       ├── presentation/  (Gradle integration tests)
│       └── fakes/         (Fake repository for testing)
├── popcornguineapig-detekt-rule/           # Detekt custom rule (separate module)
├── docs/                                    # User documentation (MkDocs)
└── ai/                                      # AI context (this directory)
```

## Platform Context

Load the platform-specific file from `ai/instructions/` before starting work:

| Platform              | File                              |
|-----------------------|-----------------------------------|
| OpenCode              | `ai/instructions/opencode.md`    |
| Claude Code           | `ai/instructions/claude.md`      |
| Cursor                | `ai/instructions/cursor.md`      |
| GitHub Copilot        | `ai/instructions/copilot.md`     |
| Gemini Code Assist    | `ai/instructions/gemini.md`      |

## Available Skills

Before starting any task, list files in `ai/skills/`, identify which covers
the task, and read it in full before proceeding.

| Skill                  | When to use                                      |
|------------------------|--------------------------------------------------|
| `popcorn-reference`    | General project reference (architecture, FAQ)    |
| `build-and-check`      | Compiling, validating build config               |
| `validate-architecture`| Analyzing code structure, layer violations       |
| `documentation-review` | Validating docs/ content, links, examples        |
| `release-notes`        | Version bumping, changelog generation            |
| `review-pr`            | Pull request review checklist                    |
| `open-pr`              | Opening pull requests, auto-generated description|
| `run-tests`            | Running tests, checking coverage                 |
| `minimum-requirements` | Analyzing deps, updating README requirements     |

## Critical Architectural Rules

1. **Three-layer clean architecture**: presentation → domain → data (presentation depends on domain; data implements domain interfaces)
2. **Domain is pure Kotlin**: NEVER import `org.gradle.api.*` in domain/
3. **Explicit API mode**: all public declarations must have explicit visibility and type annotations (`explicitApi()` in build.gradle.kts)
4. **No circular dependencies** between layers
5. **ServiceLocator.kt** wires dependencies — update it when adding new dependencies

## Implementation Workflow

1. Understand which layer the change belongs to
2. Implement: Domain (pure logic) → Data (I/O) → Presentation (Gradle integration)
3. Write/update tests (mirror source structure under `src/test/kotlin/`)
4. Validate: `./gradlew popcornguineapigplugin:koverHtmlReport`
5. Build: `./gradlew popcornguineapigplugin:build`
6. Mark task done only after tests pass and build compiles

## CI / Automation

| Workflow | Trigger | Action |
|----------|---------|--------|
| `pr.yml` | PR to main | `koverHtmlReport` on JDK 17 |
| `publish.yml` | Manual dispatch | Publish to Maven Central |
| `documentation.yml` | PR to main (docs changed) | Deploy MkDocs to GitHub Pages |
| `mega-linter.yml` | PR to main | MegaLinter Java flavor |

## PR Review Checklist

- [ ] **Architecture**: files in correct layer? No Gradle imports in domain? `ServiceLocator` updated?
- [ ] **Tests**: new tests added? Success AND failure cases? Coverage maintained?
- [ ] **Code quality**: descriptive names? Kotlin conventions? Explicit API annotations present?
- [ ] **Build**: `./gradlew popcornguineapigplugin:build` and `koverHtmlReport` pass?
