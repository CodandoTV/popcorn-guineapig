# Custom rules

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
popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.JAVA
        ),
        rules = listOf(
            MyRule(),
        )
    )
}
```
