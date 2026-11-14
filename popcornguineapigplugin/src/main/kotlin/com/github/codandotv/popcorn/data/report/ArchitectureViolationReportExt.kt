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
            it.modulePath
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
## Analysis: ${entry.key.escapeMarkdownCell()} Module

### Summary
- **Status**: ${generalSummaryStatus.toMarkdownStatus()}
- **Module**: ${entry.key.escapeMarkdownCell()}"""
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
| ${index + 1} | ${entry.key.escapeMarkdownCell()} | ${analysisItem.internalDependencyName.escapeMarkdownCell()} | ${analysisItem.ruleChecked.escapeMarkdownCell()} | ${analysisItem.ruleDescription.escapeMarkdownCell()} | ${analysisItem.result.toMarkdownStatus()} |"""
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

private val lineBreak = Regex("\\R")

/**
 * Makes a value safe to write into a Markdown table cell. An unescaped `|` ends the cell, a
 * backtick opens a code span that can swallow the rest of the row and a line break ends the row
 * entirely, so user-supplied rule patterns can break the whole table. Backslashes are escaped
 * first, otherwise a pattern that already contains `\|` would be re-escaped into an unescaped pipe.
 */
internal fun String.escapeMarkdownCell(): String = trim()
    .replace("\\", "\\\\")
    .replace("|", "\\|")
    .replace("`", "\\`")
    .replace(lineBreak, " ")
