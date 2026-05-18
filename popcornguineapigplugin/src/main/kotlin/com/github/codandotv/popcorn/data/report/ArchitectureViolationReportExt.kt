@file:Suppress("MaxLineLength", "NestedBlockDepth")
package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ViolationReportType
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import kotlin.collections.iterator

internal fun List<ArchitectureViolationReport>.toMarkdownReport(): String {
    if (isNotEmpty()) {
        val reportContent = StringBuilder()
        reportContent.append(
            "# 🍿🐹 Popcorn Guinea Pig Architecture Error Report\n\n"
        )

        val summary = groupBy {
            it.moduleName
        }

        for (entry in summary) {
            var generalSummaryStatus = ViolationReportType.PASSED
            entry.value.forEach { violationReport ->
                val isThereAnyFailure = violationReport.analysisTable.any { analysisItem ->
                    analysisItem.result == ViolationReportType.FAILED
                }
                if (isThereAnyFailure) {
                    generalSummaryStatus = ViolationReportType.FAILED
                }
            }
            reportContent.append(
                """
## Analysis: ${entry.key} Module

### Summary
- **Status**: ${generalSummaryStatus.toMarkdownStatus()}
- **Module**: ${entry.key}"""
            )

            entry.value.forEachIndexed { index, report ->
                reportContent.append(
                    """
- **Violations Found**: ${report.analysisTable.count()}

---

## Violation Details
| # | Module | Dependency | Rule | Description | Status |
| --|--------|------------|------|-------------|--------|"""
                )
                report.analysisTable.forEachIndexed { index, analysisItem ->
                    reportContent.append(
                        """
| ${index + 1} | ${entry.key} | ${analysisItem.internalDependencyName} | ${analysisItem.ruleChecked} | ${analysisItem.ruleDescription} | ${analysisItem.result.toMarkdownStatus()} |"""
                    )
                }
            }

            reportContent.append(
                """
            
-----

"""
            )
        }
        return reportContent.toString()
    } else {
        return ""
    }
}

internal fun ViolationReportType.toMarkdownStatus() = when (this) {
    ViolationReportType.PASSED -> "Passed ✅"
    ViolationReportType.FAILED -> "Failed ❌"
}
