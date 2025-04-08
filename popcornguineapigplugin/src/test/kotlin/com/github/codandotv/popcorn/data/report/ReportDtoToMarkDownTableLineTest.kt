package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
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
            ruleDescription = "DoNotWith desc",
            result = AnalysisTableResultEnumDto.PASSED
        )

        // act
        val result = tableItem.toMarkdownTableLine()
        assertEquals("| chuck-norris  | DoNotWith  | DoNotWith desc | Passed ✅|", result)
    }

    @Test
    fun `Given some table when toMarkdownTable occurs then check the markdown text`() {
        // arrange
        val expected =
            "| Dependency    | Rule           | Rule Description           | Result         |\n" +
                    "| ------------- |:--------------:|:--------------:|:--------------:|\n" +
                    "| chuck-norris  | DoNotWith  | DoNotWith desc | Passed ✅|\n" +
                    "| bruce-lee  | JustWith  | JustWith desc | Failed ❌|"

        val table = listOf(
            AnalysisTableItemDto(
                internalDependencyName = "chuck-norris",
                ruleChecked = "DoNotWith",
                ruleDescription = "DoNotWith desc",
                result = AnalysisTableResultEnumDto.PASSED
            ),
            AnalysisTableItemDto(
                internalDependencyName = "bruce-lee",
                ruleChecked = "JustWith",
                ruleDescription = "JustWith desc",
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
        val expected = ""

        val table = emptyList<AnalysisTableItemDto>()

        // act
        val result = table.toMarkdownTable()
        assertEquals(expected, result)
    }

    @Test
    fun `Given a ReportDto object when toMarkDownFormat is called then check the markdown text`() {
        // arrange
        val expected = "# Analysis -> moduleName\n\n" +
                "| Dependency    | Rule           | Rule Description           | Result         |\n" +
                "| ------------- |:--------------:|:--------------:|:--------------:|\n" +
                "| dependency  | Rule2  | Rule description | Failed ❌|\n"

        val input = ReportDto(
            analysisTable = listOf(
                AnalysisTableItemDto(
                    "dependency",
                    "Rule2",
                    "Rule description",
                    AnalysisTableResultEnumDto.FAILED
                )
            ),
            moduleName = "moduleName",
        )

        // act
        val result = input.toMarkDownFormat()

        // assert
        assertEquals(expected, result)
    }
}
