# Error reports 📃

!!!note "Error reports available >= 3.0.0" 
    Starting from version `3.0.0`, you can enable error report generation in your project.

To enable this feature, you need to run the popcorn task:

```shell
./gradlew popcornParent -PerrorReportEnabled
```

After you run the task, you can see a new report at
`/yourproject/yourmodule/build/reports/popcornguineapig/errorReport.md`.

This report offers a comprehensive analysis of the errors detected in your module.

Each analysis section is labeled with the module's Gradle project path (for example `:car:data`), not only with its leaf name. Modules that share the same name — such as `:car:data` and `:rent:data` — are evaluated and reported independently, so a violation in one of them can never be hidden by the other.

!!!note "Scoped reports available >= 3.3.0"
    When `popcornParent` 3.3.0 or later runs with `--modules`, the report contains only the modules evaluated in that run. See [Evaluating only selected modules](./1-getting-started.md#evaluating-only-selected-modules).
