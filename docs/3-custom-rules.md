# Custom rules 📝

## Create your rule

You also can create custom rules:

```kotlin
class MyRule : PopcornGuineaPigRule {
    override fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError? {
        return null
    }
}
```

## Register your rule

After that, you just need to register in the set of rules:

```kotlin
popcornGuineapigParentConfig {
    type = ProjectType.KMP

    children = listOf(
        ...
        PopcornChildConfiguration(
            moduleNameRegex = ":target-module",
            rules = listOf(
                 MyRule(),
            )
        )
}
```
