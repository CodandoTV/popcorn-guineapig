@file:Suppress("MaxLineLength")
package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport

internal fun List<ArchitectureViolationReport>.toMarkdownReport(): String {
    if (isEmpty()) {
        return ""
    }

    val reportContent = StringBuilder()
    reportContent.append(
        "# 🍿🐹 Popcorn Guinea Pig Architecture Error Report\n\n"
    )

    groupBy { it.modulePath }.forEach { (modulePath, reports) ->
        reportContent.appendModuleSummary(
            modulePath = modulePath,
            violations = reports.flatMap { it.analysisTable },
        )
    }

    return reportContent.toString()
}

/**
 * Writes one summary and one details table per module. A module can be analysed more than once, so
 * every violation of the group is merged into a single table instead of repeating the summary and
 * the table header once per analysis.
 */
private fun StringBuilder.appendModuleSummary(
    modulePath: String,
    violations: List<ViolationReportItem>,
) {
    val status = if (violations.any { it.result == ViolationReportType.FAILED }) {
        ViolationReportType.FAILED
    } else {
        ViolationReportType.PASSED
    }
    val escapedModulePath = modulePath.escapeMarkdownCell()

    append(
        """
## Analysis: $escapedModulePath Module

### Summary
- **Status**: ${status.toMarkdownStatus()}
- **Module**: $escapedModulePath
- **Violations Found**: ${violations.size}

---

## Violation Details
| # | Module | Dependency | Rule | Description | Status |
| --|--------|------------|------|-------------|--------|"""
    )

    violations.forEachIndexed { index, violation ->
        append(
            """
| ${index + 1} | $escapedModulePath | ${violation.internalDependencyName.escapeMarkdownCell()} | ${violation.ruleChecked.escapeMarkdownCell()} | ${violation.ruleDescription.escapeMarkdownCell()} | ${violation.result.toMarkdownStatus()} |"""
        )
    }

    append(
        """
            
-----

"""
    )
}

internal fun ViolationReportType.toMarkdownStatus() = when (this) {
    ViolationReportType.PASSED -> "Passed ✅"
    ViolationReportType.FAILED -> "Failed ❌"
}

private val lineBreak = Regex("\\R")

/**
 * Makes a value safe to write into a Markdown table cell. A table row is split into cells on
 * unescaped pipes before inline parsing, so an unescaped `|` ends the cell, and a line break ends
 * the row entirely; user-supplied rule patterns can break the whole table. Backslashes are escaped
 * first, otherwise a pattern that already contains `\|` would be re-escaped into an unescaped pipe.
 * Backticks are left alone: they cannot move a cell boundary, and escaping them would turn a
 * deliberate inline code span into literal backticks.
 */
internal fun String.escapeMarkdownCell(): String = trim()
    .replace("\\", "\\\\")
    .replace("|", "\\|")
    .replace(lineBreak, " ")
