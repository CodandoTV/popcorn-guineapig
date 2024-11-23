package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto

internal fun ReportDto.toMarkDownFormat(): String {
    "# $title\n" +
            "## Internal dependencies\n" +
            internalDependenciesItems.toEnumeratedMarkdownList() +
            "**Total** internal dependencies of this module is **${internalDependenciesItems.size}**\n" +

            "## Defined rules\n" +

            "### Skipped\n" +
            skippedRules.toSimpleMarkdownList() +

            "### Rules\n" +
            notSkippedRules.toSimpleMarkdownList() +

            "## Module analysis\n" +
            analysisTable.toMarkdownTable() +

            "## How can I fix it?\n" +
            howCanIFixThis.toMarkdownSection()
    return ""
}

internal fun List<String>.toSimpleMarkdownList(): String = reduce { acc, s -> "$acc- $s\n" }

internal fun List<String>.toEnumeratedMarkdownList(): String = reduceIndexed { index, acc, s ->
    val dependencyNumber = index + 1
    "$acc$dependencyNumber. $s\n"
}

internal fun List<HowCanIFixThisItemDto>.toMarkdownSection(): String = map {
    "${it.violatedRule}: ${it.message}"
}.reduce { acc, s -> "$acc- $s\n" }

internal fun List<AnalysisTableItemDto>.toMarkdownTable() =
    "| Dependency    | Rule           | Result         |\n" +
            "| ------------- |:--------------:|:--------------:|\n" +
            map { tableLine -> tableLine.toMarkdownTableLine() }
                .reduce { acc, s ->
                    acc + "\n" + s
                }

internal fun AnalysisTableItemDto.toMarkdownTableLine() =
    "| $internalDependencyName  | $ruleChecked  | ${result.toMarkdownStatus()}|"

internal fun AnalysisTableResultEnumDto.toMarkdownStatus() = when (this) {
    AnalysisTableResultEnumDto.PASSED -> "Passed ✅"
    AnalysisTableResultEnumDto.FAILED -> "Failed ❌"
    AnalysisTableResultEnumDto.SKIPPED -> "Skipped ⚠️"
}
