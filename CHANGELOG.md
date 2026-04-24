# Changelog 🍿🐹

All notable changes to **Popcorn GuineaPig** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [3.1.6] - Latest

### Changed
- Version bump via automated Fastlane `update_tag` pipeline.
- Internal version tracking moved to `popcornguineapigplugin/version.properties` (`VERSION=3.1.6`).

### Infrastructure
- Fastlane lane `update_tag` now reads `version.properties` and compares with existing Git tags using `Gem::Version` to avoid regressions.
- Git tag and commit are pushed automatically on CI after version update.

---

## [3.1.5]

### Changed
- Stability and maintenance release.
- Gradle wrapper updated to `8.4`.

### Infrastructure
- Fastlane automation improvements for release pipeline.

---

## [3.1.4]

### Changed
- Maintenance release — internal refactors and code quality improvements enforced by Detekt.

### Code Quality
- Detekt rules enforced: `MaxLineLength` (120 chars), `ReturnCount` (max 2), `TooGenericExceptionCaught`, `WildcardImport`.
- `ForbiddenComment` rule active for `TODO:`, `FIXME:` and `STOPSHIP:` markers.

---

## [3.1.3]

### Changed
- Maintenance release — dependency updates and internal cleanups.

---

## [3.1.2]

### Fixed
- Edge case in `PopcornParentTask` where `groupName` extension field was not being correctly forwarded to `PopcornTaskHelper`.

---

## [3.1.1] - 2024

### Added
- **Parent Plugin** (`io.github.codandotv.popcorngpparent`) — first stable release of the centralized rule configuration plugin.
- New Gradle task `popcornParent` registered via `PopcornGpParentPlugin`, replacing the per-module approach.
- New DSL extension `popcornGuineapigParentConfig` with the following fields:
  - `type: ProjectType` — required, defines whether the project is `JAVA`, `ANDROID` or `KMP`.
  - `children: List<PopcornChildConfiguration>` — required, list of module/rule mappings using regex.
  - `groupName: String?` — optional, overrides root project name for internal dependency detection.
- `PopcornChildConfiguration` data class introduced:
  ```kotlin
  PopcornChildConfiguration(
      moduleNameRegex = ":feature:[a-z]+",
      rules = listOf(DoNotWithRule(notWith = listOf("data")))
  )
  ```
- `PopcornParentTask` iterates over `project.allprojects` and matches each project path against `moduleNameRegex` using Kotlin `Regex`.
- Lifecycle logging at task registration and execution via `popcornLoggerLifecycle`.
- Support for `--no-configuration-cache` documented for projects with Gradle Configuration Cache enabled.

### Changed
- `popcorn` task (individual plugin) marked as **deprecated** in favor of `popcornParent`.
- Plugin ID for parent plugin: `io.github.codandotv.popcorngpparent`.

### Infrastructure
- Fastlane `Fastfile` introduced with `update_tag` lane for automated versioning and Git tagging.

---

## [3.0.3] - 2024

### Fixed
- Report generation stability fixes — `PopcornGuineapigReportException` now correctly surfaces the invalid path in its message.
- `ReportDataSource` correctly handles cases where the `reports/popcornguineapig/` directory does not yet exist (auto-creates via `mkdirs()`).
- Existing `errorReport.md` is deleted before recreation to avoid stale content.

---

## [3.0.2] - 2024

### Fixed
- `skippedRules` partition logic in `PopcornTaskHelper` — skipped errors are now correctly separated from actionable errors before triggering `error()`.
- Warning log for bypassed rules now correctly shows the rule's `simpleName`.

---

## [3.0.1] - 2024

### Fixed
- `GenerateReportUseCase` wrapped in `runCatching` inside `PopcornTaskHelper` to prevent report I/O failures from crashing the overall Gradle task.
- Logger error message added when report generation fails silently.

---

## [3.0.0] - 2024

### Added
- **Error Report generation** — new feature enabled via `-PerrorReportEnabled` Gradle property:
  ```shell
  ./gradlew popcornParent -PerrorReportEnabled
  ```
- Report is generated at: `<module>/build/reports/popcornguineapig/errorReport.md`.
- Report contains a Markdown table with columns: `Dependency | Rule | Rule Description | Result`.
- Result statuses in the report:
  - `Passed ✅`
  - `Failed ❌`
  - `Skipped ⚠️`
- New domain classes introduced:
  - `ReportData` — holds `moduleName` and `analysisTable`.
  - `AnalysisTableItemData` — holds per-violation data: `internalDependencyName`, `ruleChecked`, `ruleDescription`, `result`.
  - `AnalysisTableResultEnumData` — enum with `PASSED`, `FAILED`, `SKIPPED`.
  - `ReportInfo` — aggregates `TargetModule`, `CheckResult` and `skippedRules` for report generation.
- New use case `GenerateReportUseCase` / `GenerateReportUseCaseImpl` in the domain layer.
- New repository interface `PopcornGuineapigRepository` with `exportReport(reportPath, report)`.
- `PopcornGuineapigRepositoryImpl` delegates to `ReportDataSource` for file I/O.
- `ReportDataSource` handles directory creation, file deletion and buffered write of the Markdown content.
- Extension `ReportData.toMarkDownFormat()` generates the full Markdown report string.
- **`skippedRules`** support — allows rules to be bypassed (e.g. during architecture migration) without failing the build:
  ```kotlin
  skippedRules = listOf(DoNotWithRule::class)
  ```
  Bypassed rules appear as `Skipped ⚠️` in the report and as `WARN` in the Gradle log.
- `ServiceLocator` introduced — provides singleton instances of `CheckArchitectureUseCase`, `GenerateReportUseCase` and `PopcornGuineapigRepository`.

### Changed
- `PopcornTaskHelper` refactored to accept `generateReportUseCase` and `groupName` as constructor parameters.
- Errors are now partitioned into `skippedErrors` and `internalErrors` before logging and build failure.
- `ArchitectureViolationError.toString()` now returns `message` directly, improving log readability.

### Infrastructure
- Kover (code coverage) plugin integrated (`0.8.0`) with `koverHtmlReport` task depending on `test`.
- Detekt configuration added at `config/detekt/detekt.yml` with full ruleset.
- `kotlin { explicitApi() }` enforced in `build.gradle.kts`.

---

## [2.x and earlier]

### Core Foundation

These versions established the core engine of the plugin.

### Added
- **`PopcornGuineaPigRule`** — public interface for all architecture rules:
  ```kotlin
  interface PopcornGuineaPigRule {
      fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError?
  }
  ```
- **Built-in rules:**
  - `NoDependencyRule` — asserts a module has zero internal dependencies.
  - `DoNotWithRule(notWith: List<String>)` — asserts a module does not depend on modules matching the provided regex patterns.
  - `JustWithRule(justWith: List<String>)` — asserts a module depends on exactly the specified set of modules (sorted comparison).
- **Custom rule support** — users can implement `PopcornGuineaPigRule` and register custom rules in `PopcornChildConfiguration`.
- **`ProjectType` enum** — `JAVA`, `ANDROID`, `KMP`, each mapped to the correct Gradle configuration names:
  - `KMP` → `commonMainImplementation`, `androidMainImplementation`, `iosMainImplementation`
  - `JAVA` / `ANDROID` → `implementation`
- **`InternalDependenciesMetadata`** — domain model holding `group` and `moduleName` of each detected internal dependency.
- **`TargetModule`** — domain model holding `moduleName` and its list of `InternalDependenciesMetadata`.
- **`CheckResult`** — sealed class with `Success` and `Failure(errors)` subtypes.
- **`CheckArchitectureUseCase`** — use case that runs all rules against the internal dependencies of a module, deduplicates and sorts them before checking.
- **`ProjectExt.internalProjectDependencies()`** — Gradle `Project` extension that inspects configurations and filters dependencies whose `group` contains the project group name, identifying them as internal.
- **Colored logger extensions** on Gradle `Logger`:
  - `popcornLoggerLifecycle` → green
  - `popcornLoggerWarn` → yellow
  - `popcornLoggerError` → red
  - `popcornLoggerDebug` → yellow
  - `popcornLoggerInfo` → no color
- **`groupName` configuration** — optional field to override internal dependency group detection when the group name differs from `rootProject.name`.
- Individual plugin (`io.github.codandotv.popcorngp`) applied per-module via conventional Gradle plugins.
- Task `popcorn` registered for per-module execution.
- Published to Maven Central under `io.github.codandotv:popcornguineapig`.

---

## Notes

> ⚠️ The individual plugin (`io.github.codandotv.popcorngp`) and its `popcorn` task are **deprecated** since `3.1.0`. Migrate to the parent plugin (`io.github.codandotv.popcorngpparent`) and use `popcornParent` instead.

> ℹ️ If your project uses Gradle Configuration Phase Cache, always run with `--no-configuration-cache` until full support is added.

> 📬 Questions or contributions? Reach out to [Gabriel Moro](mailto:gabrielbronzattimoro.es@gmail.com) or [Rodrigo Vianna](mailto:rodrigo.vianna.oliveira@gmail.com).
