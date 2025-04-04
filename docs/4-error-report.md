# Error reports 📃

Starting from version `3.0.0`, you can enable error report generation in your project.

To enable this feature, you need to run the popcorn task:

```shell
./gradlew popcorn -PerrorReportEnabled
```

After you run the task, you can see a new report at
`/yourproject/yourmodule/build/reports/popcornguineapig/errorReport.md`.

This report offers a comprehensive analysis of the errors detected in your module.