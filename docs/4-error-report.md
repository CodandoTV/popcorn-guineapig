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

!!!note "Scoped reports available >= 3.3.0"
    When `popcornParent` 3.3.0 or later runs with `--modules`, the report contains only the modules evaluated in that run. See [Evaluating only selected modules](./1-getting-started.md#evaluating-only-selected-modules).
