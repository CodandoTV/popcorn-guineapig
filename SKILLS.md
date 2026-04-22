# 🍿 Popcorn Gradle Plugin - Claude Skills Guide

This document describes the available skills for working with the Popcorn Gradle Plugin using Claude Code or Cowork.

> **Skills** are reusable workflows you trigger with `/name` to execute common development, review, and validation tasks.

---

## 📋 Quick Start

To use a skill, type `/skill-name` in a conversation with Claude. Available skills:

| Skill | Description | When to use |
|-------|-------------|-------------|
| `/run-tests-popcorn` | Run tests with coverage | After changes, before commit |
| `/build-and-check` | Compile and validate build | Pre-push validation, releases |
| `/validate-architecture` | Analyze architectural structure | Code review, feature design |
| `/review-pull-request` | Systematic PR review | Before merge |

---

## 🧪 1. `/run-tests-popcorn`

**Runs unit tests and generates a Kover code coverage report.**

### When to use
- After making code changes
- Before committing or pushing
- To validate that existing tests still pass
- To debug test failures
- To check code coverage

### Main commands

**Run full suite with coverage:**
```bash
./gradlew popcornguineapigplugin:koverHtmlReport
```

**Run a specific test class:**
```bash
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest"
```

**Run a specific test method:**
```bash
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest.testValidDependencies"
```

### Results
- 📊 HTML Report: `popcornguineapigplugin/build/reports/kover/html/index.html`
- 📋 Test results: `popcornguineapigplugin/build/test-results/test/`

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

### Example: Debugging a test failure

```bash
# 1. You see a failure in "NoDependencyRule"
./gradlew popcornguineapigplugin:test --tests "NoDependencyRuleTest"

# 2. Use /validate-architecture to understand the structure
# 3. Check that the rule logic is correct
# 4. Re-run after the fix
```

---

## 🏗️ 2. `/build-and-check`

**Compiles the plugin and validates the build configuration.**

### When to use
- After changes that affect the build
- To verify there are no compilation errors
- To check that dependencies are correct
- To prepare for a release
- Pre-push validation
- Version updates

### Main commands

**Full build:**
```bash
./gradlew popcornguineapigplugin:build
```

**Clean build (removes old artifacts):**
```bash
./gradlew clean
./gradlew popcornguineapigplugin:build
```

### Important configuration files

| File | Purpose |
|------|---------|
| `popcornguineapigplugin/build.gradle.kts` | Plugin-specific configuration |
| `gradle/libs.versions.toml` | Centralized dependency versions |
| `gradle.properties` | JVM arguments, Kotlin settings |
| `settings.gradle.kts` | Repository and project setup |

### Common issues and solutions

| Issue | Solution |
|-------|----------|
| "Plugin is already compiled" | Run `./gradlew clean` first |
| "JVM memory error" | Increase `org.gradle.jvmargs` in `gradle.properties` |
| "Repository not accessible" | Check internet and URLs in `settings.gradle.kts` |
| Kotlin compiler daemon error | Expected! Intentionally disabled (KT-65761) |

### Build output
- 📦 Plugin JAR: `popcornguineapigplugin/build/libs/popcornguineapigplugin-<version>.jar`
- 📋 Metadata: `popcornguineapigplugin/build/libs/popcornguineapigplugin-<version>-gradle-metadata.json`

---

## 🏛️ 3. `/validate-architecture`

**Analyzes the code structure and validates architectural patterns.**

### When to use
- Reviewing changes that affect architecture
- Validating that new files are in the correct layer
- Analyzing import violations between layers
- Reviewing new rule implementations
- Ensuring separation of concerns
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

### Validation checklist

**File placement** ✓
```
New: Validation rule             → domain/rules/
New: Use case                    → domain/usecases/
New: Data model                  → domain/report/
New: Output formatting           → data/report/
New: Gradle integration          → presentation/
```

**Appropriate imports** ✓
```kotlin
// ✅ CORRECT
class MyRule : ArchitectureRule {
    fun validate(modules: List<Module>): List<Violation>
}

// ❌ WRONG (Domain must not import Gradle!)
import org.gradle.api.Project
class MyRule { ... }
```

**Separation of concerns** ✓
- Domain: Pure logic, no Gradle
- Data: Loading and formatting
- Presentation: Gradle integration, DSL

### Implementation patterns

**Adding a new rule:**
1. `domain/rules/MyNewRule.kt` — Implement logic
2. `src/test/kotlin/domain/rules/MyNewRuleTest.kt` — Tests
3. `domain/usecases/CheckArchitectureUseCase.kt` — Integration
4. `DependencyFactory.kt` — If injection is needed

**Adding a new report format:**
1. `data/report/MyFormatter.kt` — Implement formatting
2. `src/test/kotlin/data/report/MyFormatterTest.kt` — Tests
3. `presentation/` — Integrate with task

---

## 📝 4. `/review-pull-request`

**Performs a systematic pull request review.**

### When to use
- Reviewing PRs before merging
- Validating overall code quality
- Checking that tests were added
- Analyzing whether architecture was maintained
- Assessing the impact of changes

### Review checklist

#### ✓ Architecture
- [ ] Are new files in the correct layer?
- [ ] Are there no import violations?
- [ ] Was DependencyFactory updated (if needed)?

#### ✓ Tests
- [ ] Were new tests added?
- [ ] Do tests cover both success AND failure cases?
- [ ] Does structure mirror the source?
- [ ] Was coverage maintained or improved?

#### ✓ Code quality
- [ ] Are names descriptive?
- [ ] Does the code follow Kotlin conventions?
- [ ] Is complex logic broken into smaller functions?
- [ ] Do comments explain "why" rather than "what"?

#### ✓ Build & CI/CD
- [ ] Do tests pass (`./gradlew popcornguineapigplugin:koverHtmlReport`)?
- [ ] Does it compile without errors (`./gradlew popcornguineapigplugin:build`)?
- [ ] Does the GitHub Actions workflow pass?

#### ✓ Documentation
- [ ] Are commit messages clear?
- [ ] Was CLAUDE.md updated (if architectural changes)?
- [ ] Does the code have adequate comments?

### Red flags 🚩

Request changes if you find:
- ❌ Domain importing `org.gradle.api.*`
- ❌ Presentation with business logic
- ❌ Flaky or missing tests
- ❌ Decreased coverage
- ❌ Circular imports
- ❌ Outdated documentation

### Feedback template

```markdown
## ✅ What looks good
- Well-organized architecture
- Comprehensive tests
- Readable code

## 🔧 Request changes
- [ ] Add test for edge case with empty modules
- [ ] Update docs/2-existing-rules.md
- [ ] Improve error message for users

## ✨ Notes
- Consider refactoring MyRule into smaller functions
- Great job on the separation of concerns!
```

---

## 🚀 Common Workflows

### Implementing a new feature

```bash
# 1. Understand the architecture
/validate-architecture

# 2. Implement (Domain → Data → Presentation)
# 3. Write tests
/run-tests-popcorn

# 4. Compile locally
/build-and-check

# 5. Open PR
git push origin your-branch

# 6. Review PR before merge
/review-pull-request
```

### Debugging a failing test

```bash
# 1. Identify the failure
./gradlew popcornguineapigplugin:test

# 2. Run the specific test in isolation
/run-tests-popcorn
# And specify which test to run

# 3. Understand the architecture
/validate-architecture

# 4. Apply fix and re-run
```

### Preparing for a release

```bash
# 1. Validate architecture
/validate-architecture

# 2. Run full tests
/run-tests-popcorn

# 3. Compile
/build-and-check

# 4. Review open PRs
/review-pull-request

# 5. Update version.properties
# 6. Merge and trigger publish workflow
```

---

## 📚 Project Structure

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
│   │   └── PopcornGuineapigRepositoryImpl.kt  # Repository implementation
│   └── DependencyFactory.kt # Service locator
│
├── src/test/kotlin/         # Tests mirroring source structure
│   ├── domain/              # Rule and use case tests
│   ├── data/                # DTO and formatting tests
│   ├── presentation/        # Gradle integration tests
│   └── fakes/               # FakeRepository for mocking
│
├── build.gradle.kts         # Plugin configuration
├── gradle/libs.versions.toml # Dependency versions (centralized!)
├── gradle.properties        # JVM args, Kotlin settings
└── settings.gradle.kts      # Repositories, projects

docs/                        # User documentation
├── 1-getting-started.md
├── 2-existing-rules.md
├── 3-custom-rules.md
└── 4-error-report.md
```

---

## ❓ FAQ

**Q: Where do I add a new rule?**
A: In `domain/rules/MyNewRule.kt`. Implement the `ArchitectureRule` interface with pure logic (no Gradle). Add tests in `src/test/kotlin/domain/rules/MyNewRuleTest.kt`.

**Q: How do I test a rule without loading a real Gradle project?**
A: Use `FakePopcornGuineapigRepository`, which provides test data. See `NoDependencyRuleTest.kt` as an example.

**Q: Can I import Gradle in a rule (domain)?**
A: **No!** Domain is pure. Place code that uses the Gradle API in `data/` or `presentation/`. Pure data models (no Gradle) go in `domain/report/`; output formatting goes in `data/report/`.

**Q: Where do I add a new dependency?**
A: In `gradle/libs.versions.toml` (TOML catalog). Never add hardcoded versions in `build.gradle.kts`.

**Q: How do I update the plugin version?**
A: In `popcornguineapigplugin/version.properties`. Follow semantic versioning (MAJOR.MINOR.PATCH).

**Q: Does the plugin publish automatically to Maven Central?**
A: Yes! The GitHub Actions workflow `publish.yml` publishes when triggered. Requires configured credentials.

---

## 🔗 Important Resources

| Resource | Description |
|----------|-------------|
| **CLAUDE.md** | Full technical documentation (architecture, build, commands) |
| **CLAUDE.md** in `.claude/skills/` | Skills documentation |
| **docs/** | User documentation (rules, guides, contribution) |
| **README.md** | Project overview and getting started |
| **CONTRIBUTING.md** | Contribution guide |

---

## ✅ Before Committing

- [ ] `./gradlew popcornguineapigplugin:koverHtmlReport` ✓ (tests pass, coverage maintained)
- [ ] `./gradlew popcornguineapigplugin:build` ✓ (compiles without errors)
- [ ] `/validate-architecture` ✓ (correct structure)
- [ ] Are new files in the right place?
- [ ] Were tests added/updated?
- [ ] Was documentation updated?
- [ ] Are commit messages clear?

---

## 🎯 Next Steps

1. **Read CLAUDE.md** to understand the architecture in detail
2. **Explore skills** using `/name` in a conversation with Claude
3. **Use skills regularly** during development to maintain quality
4. **Review PRs with the checklist** from `/review-pull-request`

---

**Good luck! 🍿🐹**

For questions about the skills, ask Claude in a conversation: "Which skill should I use for ...?"
