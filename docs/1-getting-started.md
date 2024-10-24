# Getting started 🚀

## 1. Add the Plugin Dependency

Go to your build-logic folder, in the `build-logic/build.gradle.kts`, add the following dependency:

```kotlin
implementation("io.github.codandotv:popcornguineapig:<version>")
```

## 2. Apply the Plugin

You can chose a conventional gradle plugin to define your rules. 

For example, I have a gradle plugin applied to all modules `kmp-library-setup.gradle.kts`. In this conventional plugin, you can add:

```kotlin
plugins {
  ...
  id("io.github.codandotv.popcorngp")
}
```

## 3. Configure Your Architecture Rules

After apply the plugin, you can sync and define the architecture rules:

```kotlin
popcornGuineapigConfig {
    // You also can skip rules to help duing migration
    skippedRules = listOf(DoNotWithRule::class)
    
    configuration = PopcornConfiguration(
        project = PopcornProject(
            type = ProjectType.JAVA
        ),
        rules = listOf(
            NoDependencyRule(),
            DoNotWithRule(
                notWith = listOf("[a-z]+-data")
            )
        )
    )
}
```

## 4. **Run the task**

```sh
./gradlew popcorn
```

It is simple as a popcorn 🍿 + 🐹

If you want to know more details about the task, you can run:

```sh
./gradlew popcorn --info
```

Any problems you are facing, any suggestions you want to add, please feel free to [reach us out](mailto:gabrielbronzattimoro.es@gmail.com).