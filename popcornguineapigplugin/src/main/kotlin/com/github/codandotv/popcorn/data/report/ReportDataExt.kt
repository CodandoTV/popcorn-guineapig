package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.report.AnalysisTableItemData
import com.github.codandotv.popcorn.domain.report.AnalysisTableResultEnumData
import com.github.codandotv.popcorn.domain.report.ReportData

internal fun ReportData.toMarkDownFormat() = "# \uD83C\uDF7F\uD83D\uDC39 Analysis -> " +
        "${moduleName}\n\n" +
        analysisTable.toMarkdownTable() + "\n"

internal fun List<AnalysisTableItemData>.toMarkdownTable(): String {
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

internal fun AnalysisTableItemData.toMarkdownTableLine() =
    "| $internalDependencyName  | $ruleChecked  | $ruleDescription | ${result.toMarkdownStatus()}|"

internal fun AnalysisTableResultEnumData.toMarkdownStatus() = when (this) {
    AnalysisTableResultEnumData.PASSED -> "Passed ✅"
    AnalysisTableResultEnumData.FAILED -> "Failed ❌"
    AnalysisTableResultEnumData.SKIPPED -> "Skipped ⚠️"
}
