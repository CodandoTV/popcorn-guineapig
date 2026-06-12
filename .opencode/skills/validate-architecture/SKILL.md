---
name: validate-architecture
description: Use when analyzing code structure, validating architectural patterns, or during code review. Triggers on phrases like "architecture", "validate architecture", "layer", "structure".
---

# Validate Architecture

Analyzes the code structure and validates architectural patterns.

## When to use
- Reviewing changes that affect architecture
- Validating that new files are in the correct layer
- Analyzing import violations between layers
- Designing new features

## Three-layer architecture

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

## File placement rules

| New file type | Correct location |
|---------------|-----------------|
| Validation rule | `domain/rules/` |
| Use case | `domain/usecases/` |
| Pure data model / report model | `domain/report/` |
| Output formatting | `data/report/` |
| Gradle integration | `presentation/` |

## Import rules

- **Domain**: pure logic, no Gradle imports (`org.gradle.api.*`)
- **Data**: loading and formatting
- **Presentation**: Gradle integration, DSL

## Implementation patterns

**Adding a new rule:**
1. `domain/rules/MyNewRule.kt` — implement logic
2. `src/test/kotlin/domain/rules/MyNewRuleTest.kt` — tests
3. `domain/usecases/CheckArchitectureUseCase.kt` — integrate
4. `ServiceLocator.kt` — wire if injection is needed

**Adding a new report format:**
1. `data/report/MyFormatter.kt` — implement formatting
2. `src/test/kotlin/data/report/MyFormatterTest.kt` — tests
3. `presentation/` — integrate with task
