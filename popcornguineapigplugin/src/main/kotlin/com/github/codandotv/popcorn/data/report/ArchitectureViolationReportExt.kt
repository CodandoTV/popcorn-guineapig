package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport

internal fun ArchitectureViolationReport.toMarkDownFormat() = "# \uD83C\uDF7F\uD83D\uDC39 Analysis -> " +
        "${moduleName}\n\n" +
        analysisTable.toMarkdownTable() + "\n"

internal fun List<ViolationReportItem>.toMarkdownTable(): String {
    val header = "| Dependency    | Rule           | Rule Description           | Result         |\n" +
            "| ------------- |:--------------:|:--------------:|:--------------:|\n"

    val content = map { tableLine -> tableLine.toMarkdownTableLine() }
        .reduceOrNull { acc, s ->
            acc + "\n" + s
        }.orEmpty()

    return if (content.isNotEmpty()) {
        header.plus(content)
    } else ""
}

internal fun ViolationReportItem.toMarkdownTableLine() =
    "| $internalDependencyName  | $ruleChecked  | $ruleDescription | ${result.toMarkdownStatus()}|"

internal fun ViolationReportType.toMarkdownStatus() = when (this) {
    ViolationReportType.PASSED -> "Passed ✅"
    ViolationReportType.FAILED -> "Failed ❌"
}
