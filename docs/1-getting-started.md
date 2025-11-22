# Getting started 🚀

## 1. Add the Plugin Dependency

Go to your build-logic folder, in the `build-logic/build.gradle.kts`, add the following dependency:

```kotlin
implementation("io.github.codandotv:popcornguineapig:<version>")
```

## 2. Apply the Plugin

You have two ways to use PopcornGP in your project. In this section, we will present both options.

!!!warning "Deprecated - Please use the parent plugin"
    ### 2.1. Individual plugin
    
    You can chose a conventional gradle plugin to define your rules. 
    
    For example, I have a gradle plugin applied to all data modules `data-setup-plugin.gradle.kts`. In this conventional plugin, you can add:
    
    ```kotlin
    // build-logic/data-setup-plugin.gradle.kts
    plugins {
      ...
      id("io.github.codandotv.popcorngp")
    }
    ```
    
    #### Configure Your Architecture Rules
    
    Considering a data module, I want it to depend only on the domain module. This allows me to define it as follows:
    
    ```kotlin
    // build-logic/data-setup-plugin.gradle.kts
    popcornGuineapigConfig {
        // You also can skip rules to help duing migration
        skippedRules = listOf(DoNotWithRule::class)
        
        configuration = PopcornConfiguration(
            project = PopcornProject(
                type = ProjectType.JAVA
            ),
            rules = listOf(
                NoDependencyRule(),
                JustWithRule(
                    justWith = listOf("[a-z]+-domain")
                )
            )
        )
    }
    ```
    
    #### **Run the task**
    
    ```sh
    ./gradlew popcorn
    ```

### 2.2. Parent plugin

Starting with version v3.1.0, we introduced support for a parent plugin, enabling developers to define all project rules in a centralized location.

Let's create a plugin that will be applied to the root build.gradle file of our project:

```kotlin
// build-logic/popcorngp-setup-plugin.gradle.kts
plugins {
  ...
  id("io.github.codandotv.popcorngpparent")
}
```

#### Configure All Your Architecture Rules

Now, we can specify in a single place all the rules that we want to apply to our project.

```kotlin
// build-logic/popcorngp-setup-plugin.gradle.kts
popcornGuineapigParentConfig {
    type = ProjectType.KMP

    children = listOf(
        PopcornChildConfiguration(
            moduleNameRegex = ":util:[a-z]+",
            rules = listOf(
                NoDependencyRule(),
            ),
        ),
        PopcornChildConfiguration(
            moduleNameRegex = ":feature:[a-z]+",
            rules = listOf(
                DoNotWithRule(
                    notWith = listOf("data"),
                ),
            ),
        ),
```

Don't forget to apply the plugin to the root build.gradle file:

```kotlin
// root/build.gradle.kts
plugins {
    id("popcorngp-setup-plugin")
    ...
}
```

#### **Run the task**

```sh
./gradlew popcornParent
```

---

It is simple as a popcorn 🍿 + 🐹

Any problems you are facing, any suggestions you want to add, please feel free to [reach us out](mailto:gabrielbronzattimoro.es@gmail.com).