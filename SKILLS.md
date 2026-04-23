# Popcorn Gradle Plugin - Claude Skills Guide

This document describes the available skills for working with the Popcorn Gradle Plugin using Claude Code, and serves as the primary technical reference for the project.

> **Skills** are reusable workflows you trigger with `/name` to execute common development, review, and validation tasks.

---

## Quick Start

To use a skill, type `/skill-name` in a conversation with Claude. Available skills:

| Skill | Description | When to use |
|-------|-------------|-------------|
| `/run-tests-popcorn` | Run tests with coverage | After changes, before commit |
| `/build-and-check` | Compile and validate build | Pre-push validation, releases |
| `/validate-architecture` | Analyze architectural structure | Code review, feature design |
| `/review-pull-request` | Systematic PR review | Before merge |

---

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

### Key entry points

- **Plugin registration**: `PopcornGpParentPlugin.apply()` — registers `popcornParent` task
- **Main task**: `popcornParent` — runs architecture validation
- **Configuration**: via `popcorn { }` DSL block in build.gradle files

---

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

### Important configuration files

| File | Purpose |
|------|---------|
| `popcornguineapigplugin/build.gradle.kts` | Plugin-specific configuration |
| `gradle/libs.versions.toml` | Centralized dependency versions |
| `gradle.properties` | JVM arguments, Kotlin settings |
| `settings.gradle.kts` | Repositories and project setup |

---

## CI/CD

- **PR workflow** (`pr.yml`): runs on PRs to `main`, executes `./gradlew popcornguineapigplugin:koverHtmlReport`, requires JDK 17, uses Gradle caching
- **Publish workflow** (`publish.yml`): publishes to Maven Central when triggered; requires configured credentials

### Version management

- Version stored in `popcornguineapigplugin/version.properties`
- Applied to published artifact at build time
- Follow semantic versioning (MAJOR.MINOR.PATCH)

---

## 1. `/run-tests-popcorn`

**Runs unit tests and generates a Kover code coverage report.**

### When to use
- After making code changes
- Before committing or pushing
- To validate that existing tests still pass
- To check code coverage

### Main commands

```bash
# Full suite with coverage
./gradlew popcornguineapigplugin:koverHtmlReport

# Specific test class
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest"

# Specific test method
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest.testValidDependencies"
```

### Results
- HTML Report: `popcornguineapigplugin/build/reports/kover/html/index.html`
- Test results: `popcornguineapigplugin/build/test-results/test/`

### Test structure
```
Domain Layer Tests
├── rules/               # NoDependencyRule, JustWithRule, DoNotWithRule
├── usecases/           # CheckArchitectureUseCase, GenerateReportUseCase
└── input/              # ProjectType, domain models

Data Layer Tests
├── report/             # Report formatting (Markdown, tables)
└── dto/                # Data transformation

Presentation Layer Tests
└── Gradle integration  # Tasks, plugin registration
```

### Testing approach
- Framework: JUnit with Kotlin Test
- Coverage tool: Kover
- Tests mirror source structure under `src/test/kotlin/`
- Use `FakePopcornGuineapigRepository` to test without a real Gradle project — see `NoDependencyRuleTest.kt` as example

---

## 2. `/build-and-check`

**Compiles the plugin and validates the build configuration.**

### When to use
- After changes that affect the build
- To verify there are no compilation errors
- To prepare for a release

### Main commands

```bash
# Full build
./gradlew popcornguineapigplugin:build

# Clean build
./gradlew clean
./gradlew popcornguineapigplugin:build
```

### Common issues and solutions

| Issue | Solution |
|-------|----------|
| "Plugin is already compiled" | Run `./gradlew clean` first |
| "JVM memory error" | Increase `org.gradle.jvmargs` in `gradle.properties` |
| "Repository not accessible" | Check internet and URLs in `settings.gradle.kts` |
| Kotlin compiler daemon error | Expected — intentionally disabled (KT-65761) |

### Build output
- Plugin JAR: `popcornguineapigplugin/build/libs/popcornguineapigplugin-<version>.jar`

---

## 3. `/validate-architecture`

**Analyzes the code structure and validates architectural patterns.**

### When to use
- Reviewing changes that affect architecture
- Validating that new files are in the correct layer
- Analyzing import violations between layers
- Designing new features

### Three-layer architecture

```
┌─────────────────────────────────────────┐
│   PRESENTATION LAYER (Gradle API)       │
│   ├── PopcornGpParentPlugin            │
│   ├── Tasks                             │
│   └── DSL configuration                 │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│   DOMAIN LAYER (Pure logic)             │
│   ├── Rules (rule validation)           │
│   ├── UseCases (orchestration)          │
│   ├── Models (ProjectType, etc)         │
│   ├── Repository (interface contract)   │
│   └── Report (ReportData, etc)          │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│   DATA LAYER (I/O and transformation)   │
│   ├── RepositoryImpl (implementation)   │
│   └── ReportDataSource (formatting)     │
└─────────────────────────────────────────┘
```

### File placement rules

| New file type | Correct location |
|---------------|-----------------|
| Validation rule | `domain/rules/` |
| Use case | `domain/usecases/` |
| Pure data model / report model | `domain/report/` |
| Output formatting | `data/report/` |
| Gradle integration | `presentation/` |

### Import rules

```kotlin
// CORRECT
class MyRule : ArchitectureRule {
    fun validate(modules: List<Module>): List<Violation>
}

// WRONG — domain must not import Gradle
import org.gradle.api.Project
class MyRule { ... }
```

- **Domain**: pure logic, no Gradle imports
- **Data**: loading and formatting
- **Presentation**: Gradle integration, DSL

### Implementation patterns

**Adding a new rule:**
1. `domain/rules/MyNewRule.kt` — implement logic
2. `src/test/kotlin/domain/rules/MyNewRuleTest.kt` — tests
3. `domain/usecases/CheckArchitectureUseCase.kt` — integrate
4. `ServiceLocator.kt` — wire if injection is needed

**Adding a new report format:**
1. `data/report/MyFormatter.kt` — implement formatting
2. `src/test/kotlin/data/report/MyFormatterTest.kt` — tests
3. `presentation/` — integrate with task

---

## 4. `/review-pull-request`

**Performs a systematic pull request review.**

### When to use
- Reviewing PRs before merging
- Validating overall code quality
- Checking that tests were added
- Assessing the impact of changes

### Review checklist

#### Architecture
- [ ] Are new files in the correct layer?
- [ ] Are there no import violations (domain importing Gradle)?
- [ ] Was `ServiceLocator.kt` updated if new dependencies were added?

#### Tests
- [ ] Were new tests added?
- [ ] Do tests cover both success AND failure cases?
- [ ] Does structure mirror the source?
- [ ] Was coverage maintained or improved?

#### Code quality
- [ ] Are names descriptive?
- [ ] Does the code follow Kotlin conventions?
- [ ] Do comments explain "why" rather than "what"?

#### Build & CI/CD
- [ ] Do tests pass (`./gradlew popcornguineapigplugin:koverHtmlReport`)?
- [ ] Does it compile without errors (`./gradlew popcornguineapigplugin:build`)?
- [ ] Does the GitHub Actions workflow pass?

#### Documentation
- [ ] Are commit messages clear?
- [ ] Was SKILLS.md updated if architectural changes were made?

### Red flags

Request changes if you find:
- Domain importing `org.gradle.api.*`
- Presentation layer containing business logic
- Flaky or missing tests
- Decreased coverage
- Circular imports

### Feedback template

```markdown
## What looks good
- Well-organized architecture
- Comprehensive tests

## Request changes
- [ ] Add test for edge case with empty modules
- [ ] Update docs/2-existing-rules.md

## Notes
- Consider refactoring MyRule into smaller functions
```

---

## Common Workflows

### Implementing a new feature

```bash
# 1. Understand the architecture
/validate-architecture

# 2. Implement (Domain → Data → Presentation)
# 3. Write tests
/run-tests-popcorn

# 4. Compile locally
/build-and-check

# 5. Open PR and review
/review-pull-request
```

### Debugging a failing test

```bash
# 1. Run the full suite to identify failures
./gradlew popcornguineapigplugin:test

# 2. Run the specific test in isolation
./gradlew popcornguineapigplugin:test --tests "FailingTestClass"

# 3. Fix and re-run
```

### Preparing for a release

```bash
# 1. Validate architecture
/validate-architecture

# 2. Run full tests
/run-tests-popcorn

# 3. Compile
/build-and-check

# 4. Update popcornguineapigplugin/version.properties
# 5. Merge and trigger publish.yml workflow
```

---

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

---

## Before Committing

- [ ] `./gradlew popcornguineapigplugin:koverHtmlReport` passes (tests pass, coverage maintained)
- [ ] `./gradlew popcornguineapigplugin:build` compiles without errors
- [ ] `/validate-architecture` confirms correct structure
- [ ] New files are in the right layer
- [ ] Tests were added/updated
- [ ] Commit messages are clear
