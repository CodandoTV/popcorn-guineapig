package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.ReportDto

internal fun ReportDto.toMarkDownFormat() = "# \uD83C\uDF7F\uD83D\uDC39 Analysis -> " +
        "${moduleName}\n\n" +
        analysisTable.toMarkdownTable() + "\n"

internal fun List<AnalysisTableItemDto>.toMarkdownTable(): String {
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

internal fun AnalysisTableItemDto.toMarkdownTableLine() =
    "| $internalDependencyName  | $ruleChecked  | $ruleDescription | ${result.toMarkdownStatus()}|"

internal fun AnalysisTableResultEnumDto.toMarkdownStatus() = when (this) {
    AnalysisTableResultEnumDto.PASSED -> "Passed ✅"
    AnalysisTableResultEnumDto.FAILED -> "Failed ❌"
    AnalysisTableResultEnumDto.SKIPPED -> "Skipped ⚠️"
}
