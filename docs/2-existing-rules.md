# Existing rules 📐

We have several rules already defined in the plugin. You can create some rule too, take a look in our section about [contributions](./6-contributions.md).

## DoNotWithRule

The `DoNotWithRule` specifies that some modules should not depend on certain modules. For example:

```kotlin
popcornGuineapigParentConfig {
    type = ProjectType.KMP

    children = listOf(
        PopcornChildConfiguration(
            moduleNameRegex = ":feature:feature-[a-z]+",
            rules = listOf(
                DoNotWithRule(
                    notWith = listOf("[a-z]+-data")
                )
            )
        ),
        ...
    )
}
```

In this case, any feature modules (`:feature:feature-dog`, `:feature:feature-guineapig`, and others) should not depend on any module whose name includes 'something-data' (e.g. `dog-data`, `guineapig-data`, and others).

## JustWithRule

The `JustWithRule` specifies that some modules should depend on certain modules. For example:

```kotlin
popcornGuineapigParentConfig {
    type = ProjectType.KMP

    children = listOf(
        ...
        PopcornChildConfiguration(
            moduleNameRegex = ":[a-z]+:data",
            rules = listOf(
                JustWithRule(
                    justWith = listOf("domain")
                )
            )
        )
        ...
    )
}
```

In this case, all data modules (e.g. `:car:data`, `:rent:data`, and others) should depend only on a domain module.

The `justWith` entries also support regex patterns. For example, to restrict modules to depend only on modules whose names match a pattern:

> **Note:** Regex pattern support in `JustWithRule.justWith` is available from version 3.2.2 onward.

```kotlin
JustWithRule(
    justWith = listOf("domain", "feature-.*")
)
```

Each dependency must match at least one `justWith` pattern. Like the `DoNotWithRule`, each entry is treated as a regular expression — so `"domain"` matches exactly, while `"feature-.*"` matches any module name starting with `feature-`. Dependencies that don't match any `justWith` pattern are flagged as violations.

## NoDependencyRule

The `NoDependencyRule` ensures that some modules remains free of any dependencies. For example:

```kotlin
popcornGuineapigParentConfig {
    type = ProjectType.KMP

    children = listOf(
        ...
        PopcornChildConfiguration(
            moduleNameRegex = ":util:[a-z]+",
            rules = listOf(
                NoDependencyRule(),
            ),
        ),
        ...
    )
}
```

In this case, all util modules (e.g. `:util:media`, `:util:camera`, and others) should be free of any dependencies.

## PopcornGuineaPigRule

Take a look in our documentation about how you can create your [custom rule](./3-custom-rules.md).
