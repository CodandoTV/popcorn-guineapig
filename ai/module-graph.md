# Popcorn GuineaPig — Module Dependency Graph

## Gradle modules

```
PopcornGuineaPig (root)          # Aggregator, no source
├── popcornguineapigplugin        # Plugin source + tests
└── popcornguineapig-detekt-rule  # Detekt custom rule (separate module)
```

## popcornguineapigplugin — Internal layer dependencies

```
presentation/  (Gradle API, tasks, DSL)
    ↕ depends on domain/ + uses Gradle plugin API
domain/        (Pure Kotlin — NO Gradle imports)
    ↕ defines repository interface consumed by data/
data/          (I/O, file writing, formatting)
    implements domain/ repository + uses domain/ models
```

## File placement rules

| File type | Correct location |
|-----------|-----------------|
| Validation rule | `domain/rules/` |
| Use case | `domain/usecases/` |
| Pure data model / report model | `domain/models/` |
| Repository interface | `domain/PopcornGuineapigRepository.kt` |
| Repository implementation | `data/PopcornGuineapigRepositoryImpl.kt` |
| Output formatting | `data/report/` |
| Gradle integration | `presentation/` |
| Dependency wiring | `ServiceLocator.kt` |

## Import rules

- **Domain**: NO imports from `org.gradle.api.*` — zero Gradle dependency
- **Data**: imports domain models, does I/O and formatting
- **Presentation**: imports domain use cases, integrates with Gradle task API

## Key entry points

| What | Where |
|------|-------|
| Plugin class | `presentation/PopcornGpParentPlugin.kt` |
| Main task | `presentation/tasks/PopcornParentTask.kt` |
| Service wiring | `ServiceLocator.kt` |
| All three rules | `domain/rules/` (NoDependencyRule, JustWithRule, DoNotWithRule) |
| Custom rule interface | `domain/rules/PopcornGuineaPigRule.kt` |
