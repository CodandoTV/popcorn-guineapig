package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto

internal fun ReportDto.toMarkDownFormat() = "# $title\n\n" +
        "## Internal dependencies\n\n" +
        internalDependenciesItems.toEnumeratedMarkdownList() + "\n\n" +
        "**Total** internal dependencies of this module is **${internalDependenciesItems.size}**\n\n" +
        "## Defined rules\n\n" +
        "### Skipped\n\n" +
        skippedRules.toSimpleMarkdownList() +"\n\n" +
        "### Rules\n\n" +
        notSkippedRules.toSimpleMarkdownList() + "\n\n" +
        "## Module analysis\n\n" +
        analysisTable.toMarkdownTable() + "\n\n" +
        "## How can I fix it?\n\n" +
        howCanIFixThis.toMarkdownSection()

internal fun List<String>.toSimpleMarkdownList(): String = map { "- $it" }.reduce { acc, s -> "$acc\n$s" }

internal fun List<String>.toEnumeratedMarkdownList(): String = mapIndexed { index: Int, s: String ->
    "${index + 1}. $s"
}.reduce { acc, s -> "$acc\n$s" }

internal fun List<HowCanIFixThisItemDto>.toMarkdownSection(): String = map {
    "- ${it.violatedRule}: ${it.message}"
}.reduce { acc, s -> "$acc\n$s" }

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
