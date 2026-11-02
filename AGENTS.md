# Popcorn GuineaPig — AI Context

Gradle plugin that enforces architectural rules in multi-module projects.
Validates module dependency graphs against user rules (`NoDependency`, `JustWith`,
`DoNotWith`) and generates error/metrics reports. Published to Maven Central as
`io.github.codandotv:popcornguineapig`; Gradle plugin id `io.github.codandotv.popcorngpparent`.

## Repo shape

- Single Gradle module: `popcornguineapigplugin/`, wired as an included build from
  `settings.gradle.kts` via `includeBuild(...)`. There is no detekt-rule module.
- Detekt is config-only: `config/detekt/detekt.yml`, run via `scripts/detektcheck.sh`.
- `docs/` is built with Zensical (reads `mkdocs.yml`) and deployed to GitHub Pages.
- Version catalog `gradle/libs.versions.toml`; publishing props in
  `popcornguineapigplugin/gradle.properties`.

## Layers

`presentation/` (Gradle API, tasks, DSL) → `domain/` (pure Kotlin) → `data/`
(I/O, formatting). `data` implements `domain/PopcornGuineapigRepository.kt`.

| File type | Location |
|-----------|----------|
| Validation rule | `domain/rules/` |
| Use case | `domain/usecases/` |
| Pure / report model | `domain/models/` |
| Repository interface | `domain/PopcornGuineapigRepository.kt` |
| Repository impl / formatting | `data/`, `data/report/` |
| Gradle integration | `presentation/` |
| Dependency wiring | `ServiceLocator.kt` |

Entry points: plugin `presentation/PopcornGpParentPlugin.kt`, task
`presentation/tasks/PopcornParentTask.kt`.

## Commands

```bash
./gradlew popcornguineapigplugin:build            # compile (no separate lint/typecheck task)
./gradlew popcornguineapigplugin:koverHtmlReport  # tests + coverage — the CI gate (pr.yml)
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest"
bash scripts/detektcheck.sh                        # Detekt locally (MegaLinter uses same config)
```

- `koverHtmlReport` depends on `test`; run it before `build` when checking a change.
- Release: bump `VERSION` in `popcornguineapigplugin/version.properties` (currently 3.2.3),
  prepend to `CHANGELOG.md`, then dispatch `publish.yml` manually.

## Hard rules

1. Layer direction `presentation → domain → data`; `data` implements domain interfaces. No cycles.
2. `domain/` is pure Kotlin — NEVER import `org.gradle.api.*` there.
3. Explicit API mode (`explicitApi()`): every public declaration needs `public` + explicit return type.
4. Update `ServiceLocator.kt` whenever you add a repository/use-case dependency.

## Testing

- JUnit 4 + `kotlin.test`; tests mirror `src/main` under `src/test/kotlin/`.
- Use fakes in `src/test/kotlin/.../fakes/` (e.g. `FakePopcornGuineapigRepository`) instead of real Gradle projects.
- `kotlin.native.disableCompilerDaemon=true` is intentional (KT-65761) — daemon errors are expected.

## Plugin behavior (consumer-facing)

- Tasks: `popcornParent`, `popcornModuleMetrics`, `installPopcornSkill`.
- `popcornParent` reads `-PerrorReportEnabled`; on projects with configuration cache run
  with `--no-configuration-cache` (see README).

## OpenCode setup

`opencode.json` loads this file (`instructions`) and discovers skills in
`.opencode/skills/` (`skills.paths`). Load the matching skill before a task:
`popcorn-reference`, `build-and-check`, `run-tests`, `validate-architecture`,
`documentation-review`, `release-notes`, `review-pr`, `open-pr`,
`minimum-requirements`.

To add a skill, create `.opencode/skills/<name>/SKILL.md` (discovery via the
configured path is automatic) and add it to the list above. This repo carries no
per-assistant files (Claude Code, Cursor, Copilot, Gemini, …) beyond the DSH
bridge below.

## DeepSeek Harness setup

`.dsh/skills` is a relative symlink to `.opencode/skills`, so DSH discovers the
same bundles at `<projectRoot>/.dsh/skills/<name>/SKILL.md` with no duplicated
content and no machine-specific configuration (the project root is the nearest
`.git` ancestor). Keep it a committed symlink; never copy skills under `.dsh/`.
The DSH watcher picks up new skills without a restart.

Frontmatter must stay portable: both tools require `name` (kebab-case) and
`description`. Avoid other keys where possible — `disable-model-invocation` is
DSH-only and `permission` blocks are OpenCode-only. `popcorn-reference`'s
"automatically loaded on session start" holds for OpenCode only; in DSH it is
loaded on demand like every other skill.

## PR checklist

- [ ] Architecture: correct layer, no Gradle imports in `domain/`, `ServiceLocator` updated
- [ ] Tests added for success AND failure cases; coverage maintained
- [ ] Explicit API annotations present; descriptive names
- [ ] `./gradlew popcornguineapigplugin:build` and `koverHtmlReport` pass
