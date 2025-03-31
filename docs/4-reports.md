# Reports 📃

Starting from version `1.1.0`, you can enable report generation in your project.

To configure this feature, add the following setting to your `build.gradle.kts` file:

```kotlin
popcornGuineapigConfig {
    configuration = PopcornConfiguration(
        ...
        hasReportEnabled = true
    )
}
```

After you run the task, you can see a new report at 
`/yourproject/yourmodule/build/reports/popcornguineapig/YYYY-MM-DD_HH-MM-SS.md`.

This report provides detailed insights into the module analyzed by PopcornGuineapig Check, including its 
internal dependencies and the results of evaluating their relationships.