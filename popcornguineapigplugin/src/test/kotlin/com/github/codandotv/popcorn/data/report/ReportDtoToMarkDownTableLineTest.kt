package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto
import org.junit.Test
import kotlin.test.assertEquals

class ReportDtoToMarkDownTableLineTest {
    @Test
    fun `Given some table item when toMarkdownTableLine occurs then check the markdown text`() {
        // arrange
        val tableItem = AnalysisTableItemDto(
            internalDependencyName = "chuck-norris",
            ruleChecked = "DoNotWith",
            result = AnalysisTableResultEnumDto.PASSED
        )

        // act
        val result = tableItem.toMarkdownTableLine()
        assertEquals("| chuck-norris  | DoNotWith  | Passed ✅|", result)
    }

    @Test
    fun `Given some table when toMarkdownTable occurs then check the markdown text`() {
        // arrange
        val expected = "| Dependency    | Rule           | Result         |\n" +
                "| ------------- |:--------------:|:--------------:|\n" +
                "| chuck-norris  | DoNotWith  | Passed ✅|\n" +
                "| bruce-lee  | JustWith  | Failed ❌|"

        val table = listOf(
            AnalysisTableItemDto(
                internalDependencyName = "chuck-norris",
                ruleChecked = "DoNotWith",
                result = AnalysisTableResultEnumDto.PASSED
            ),
            AnalysisTableItemDto(
                internalDependencyName = "bruce-lee",
                ruleChecked = "JustWith",
                result = AnalysisTableResultEnumDto.FAILED
            )
        )

        // act
        val result = table.toMarkdownTable()
        assertEquals(expected, result)
    }

    @Test
    fun `Given an empty table when toMarkdownTable occurs then check the markdown text`() {
        // arrange
        val expected = "| Dependency    | Rule           | Result         |\n" +
                "| ------------- |:--------------:|:--------------:|\n"

        val table = emptyList<AnalysisTableItemDto>()

        // act
        val result = table.toMarkdownTable()
        assertEquals(expected, result)
    }

    @Test
    fun `Given a ReportDto object when toMarkDownFormat is called then check the markdown text`() {
        // arrange
        val expected = "# dependency module\n\n" +
                "## Internal dependencies\n\n" +
                "1. dependency\n\n" +
                "**Total** internal dependencies of this module is **1**\n\n" +
                "## Defined rules\n\n" +
                "### Skipped\n\n" +
                "- Rule4\n\n" +
                "### Rules\n\n" +
                "- Rule2\n\n" +
                "## Module analysis\n\n" +
                "| Dependency    | Rule           | Result         |\n" +
                "| ------------- |:--------------:|:--------------:|\n" +
                "| dependency  | Rule2  | Failed ❌|\n\n" +
                "## How can I fix it?\n\n" +
                "- Rule2: You should do something about that"

        val input = ReportDto(
            howCanIFixThis = listOf(
                HowCanIFixThisItemDto(
                    violatedRule = "Rule2",
                    message = "You should do something about that"
                )
            ),
            analysisTable = listOf(
                AnalysisTableItemDto(
                    "dependency", "Rule2", AnalysisTableResultEnumDto.FAILED
                )
            ),
            skippedRules = listOf("Rule4"),
            notSkippedRules = listOf("Rule2"),
            internalDependenciesItems = listOf("dependency"),
            moduleName = "dependency",
        )

        // act
        val result = input.toMarkDownFormat()

        // assert
        assertEquals(expected, result)
    }
}
