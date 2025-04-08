package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.ReportDto

internal fun ReportDto.toMarkDownFormat() = "# $moduleName module\n\n" +
        "## Module analysis\n\n" +
        analysisTable.toMarkdownTable() + "\n"

internal fun List<AnalysisTableItemDto>.toMarkdownTable(): String {
    val header = "| Dependency    | Rule           | Result         |\n" +
            "| ------------- |:--------------:|:--------------:|\n"

    val content = map { tableLine -> tableLine.toMarkdownTableLine() }
        .reduceOrNull { acc, s ->
            acc + "\n" + s
        }.orEmpty()

    return if (content.isNotEmpty()) {
        header.plus(content)
    } else ""
}

internal fun AnalysisTableItemDto.toMarkdownTableLine() =
    "| $internalDependencyName  | $ruleChecked  | ${result.toMarkdownStatus()}|"

internal fun AnalysisTableResultEnumDto.toMarkdownStatus() = when (this) {
    AnalysisTableResultEnumDto.PASSED -> "Passed ✅"
    AnalysisTableResultEnumDto.FAILED -> "Failed ❌"
    AnalysisTableResultEnumDto.SKIPPED -> "Skipped ⚠️"
}
