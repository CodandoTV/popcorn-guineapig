package com.github.codandotv.popcorn.data

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto
import java.io.File

internal class ReportDataSource {
    fun export(fullPath: String, reportDto: ReportDto) {
        val reportPath = fullPath + File.separator + "popcornguineapig" + File.separator + System.currentTimeMillis()

        val reportDir = File(reportPath)

        if (!reportDir.exists()) {
            reportDir.mkdirs()
        }

        val reportFile = File(reportPath + File.separator + "report.md")

        val reportContent = reportDto.toMarkDownFormat()

        reportFile.bufferedWriter().use {
            it.write(reportContent)
        }
    }
}

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

private fun List<String>.toSimpleMarkdownList(): String = reduce { acc, s -> "$acc- $s\n" }

private fun List<String>.toEnumeratedMarkdownList(): String = reduceIndexed { index, acc, s ->
    val dependencyNumber = index + 1
    "$acc$dependencyNumber. $s\n"
}

private fun List<HowCanIFixThisItemDto>.toMarkdownSection(): String = map {
    "${it.violatedRule}: ${it.message}"
}.reduce { acc, s -> "$acc- $s\n" }

private fun List<AnalysisTableItemDto>.toMarkdownTable() =
    "| Dependency    | Rule           | Result         |\n" +
            "| ------------- |:--------------:|:--------------:|\n" +
            map { tableLine -> tableLine.toMarkdownTableLine() }
                .reduce { acc, s ->
                    "$acc$s"
                }

private fun AnalysisTableItemDto.toMarkdownTableLine() =
    "| $internalDependencyName   | $ruleChecked  | ${result.toMarkdownStatus()}|\n"

private fun AnalysisTableResultEnumDto.toMarkdownStatus() = when (this) {
    AnalysisTableResultEnumDto.PASSED -> "Passed ✅"
    AnalysisTableResultEnumDto.FAILED -> "Failed ❌"
    AnalysisTableResultEnumDto.SKIPPED -> "Skipped ⚠️"
}
