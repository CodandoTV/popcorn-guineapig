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

Each `InternalDependenciesMetadata` exposes:

| Property | Description |
|----------|-------------|
| `group` | Dependency group, or `null` when the dependency has no group. |
| `moduleName` | Dependency leaf name (for example `data`). Built-in rules match their patterns against this value. |
| `modulePath` | Gradle project path of the dependency (for example `:car:data`) when it is a project dependency. It is `null` for dependencies known only by coordinates. |

`modulePath` lets custom rules distinguish dependencies that share a leaf name. Built-in rules (`DoNotWithRule`, `JustWithRule`) keep matching `moduleName` for backward compatibility.

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
