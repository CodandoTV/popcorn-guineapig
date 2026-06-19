---
name: review-pr
description: Use when performing pull request reviews before merging. Triggers on phrases like "review PR", "review pull request", "code review", "PR review".
---

# Review Pull Request

Performs a systematic pull request review.

## When to use
- Reviewing PRs before merging
- Validating overall code quality
- Checking that tests were added
- Assessing the impact of changes

## Review checklist

### Architecture
- [ ] Are new files in the correct layer?
- [ ] Are there no import violations (domain importing Gradle)?
- [ ] Was `ServiceLocator.kt` updated if new dependencies were added?

### Tests
- [ ] Were new tests added?
- [ ] Do tests cover both success AND failure cases?
- [ ] Does structure mirror the source?
- [ ] Was coverage maintained or improved?

### Code quality
- [ ] Are names descriptive?
- [ ] Does the code follow Kotlin conventions?

### Build & CI/CD
- [ ] Do tests pass (`./gradlew popcornguineapigplugin:koverHtmlReport`)?
- [ ] Does it compile without errors (`./gradlew popcornguineapigplugin:build`)?
- [ ] Does the GitHub Actions workflow pass?

### Documentation
- [ ] Are commit messages clear?

## Red flags

Request changes if you find:
- Domain importing `org.gradle.api.*`
- Presentation layer containing business logic
- Flaky or missing tests
- Decreased coverage
- Circular imports

## Feedback template

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
