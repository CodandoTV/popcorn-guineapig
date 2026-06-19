---
name: run-tests
description: Use when running tests, checking code coverage after changes, or before committing. Triggers on phrases like "run tests", "kover", "test coverage".
---

# Run Tests & Coverage

Run unit tests and generate a Kover code coverage report for the Popcorn Gradle Plugin.

## When to use
- After making code changes
- Before committing or pushing
- To validate that existing tests still pass
- To check code coverage

## Main commands

```bash
# Full suite with coverage
./gradlew popcornguineapigplugin:koverHtmlReport

# Specific test class
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest"

# Specific test method
./gradlew popcornguineapigplugin:test --tests "com.github.codandotv.popcorn.domain.rules.NoDependencyRuleTest.testValidDependencies"
```

## Results
- HTML Report: `popcornguineapigplugin/build/reports/kover/html/index.html`
- Test results: `popcornguineapigplugin/build/reports/tests/test/index.html`

## Test structure
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

## Testing approach
- Framework: JUnit with Kotlin Test
- Coverage tool: Kover
- Tests mirror source structure under `src/test/kotlin/`
- Use `FakePopcornGuineapigRepository` to test without a real Gradle project
