# Existing rules 📐

We have several rules already defined in the plugin. You can create some rule too, take a look in our section about [contributions](./5-contributions.md).

## DoNotWithRule

The `DoNotWithRule` specifies that the target module should not depend on certain modules. For example:

```kotlin
popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        ...
        rules = listOf(
            DoNotWithRule(
                notWith = listOf("[a-z]+-data")
            )
        )
    )
}
```

In this case, the current module should not depend on any module whose name includes 'something-data'. A good rule if you are dealing with presentation modules, you can force the developer use a domain module instead of a data module.

## JustWithRule

The `JustWithRule` specifies that the target module should depend on certain modules. For example:

```kotlin
popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        ...
        rules = listOf(
            JustWithRule(
                justWith = listOf("util-[a-z]+")
            )
        )
    )
}
```

In this case, the current module should depend on any module whose name includes 'util-something'. This rule is useful if you need to enforce that a module depends only on utility modules.

## NoDependencyRule

The `NoDependencyRule` ensures that the target module remains free of any dependencies. For example:

```kotlin
popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        ...
        rules = listOf(
            NoDependencyRule()
        )
    )
}
```

In this case, the current module should be free of any dependencies. This rule is useful if you want to make sure your domain modules are free of any dependencies.

## PopcornGuineaPigRule

Take a look in our documentation about how you can create your [custom rule](./3-custom-rules.md).
