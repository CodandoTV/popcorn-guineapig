package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.report.AnalysisTableItemData
import com.github.codandotv.popcorn.domain.report.AnalysisTableResultEnumData
import com.github.codandotv.popcorn.domain.report.ReportData
import org.junit.Test
import kotlin.test.assertEquals

class ReportDataToMarkDownTableLineTest {
    @Test
    fun `Given some table item when toMarkdownTableLine occurs then check the markdown text`() {
        // arrange
        val tableItem = AnalysisTableItemData(
            internalDependencyName = "chuck-norris",
            ruleChecked = "DoNotWith",
            ruleDescription = "DoNotWith desc",
            result = AnalysisTableResultEnumData.PASSED
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
            AnalysisTableItemData(
                internalDependencyName = "chuck-norris",
                ruleChecked = "DoNotWith",
                ruleDescription = "DoNotWith desc",
                result = AnalysisTableResultEnumData.PASSED
            ),
            AnalysisTableItemData(
                internalDependencyName = "bruce-lee",
                ruleChecked = "JustWith",
                ruleDescription = "JustWith desc",
                result = AnalysisTableResultEnumData.FAILED
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

        val table = emptyList<AnalysisTableItemData>()

        // act
        val result = table.toMarkdownTable()
        assertEquals(expected, result)
    }

    @Test
    fun `Given a ReportDto object when toMarkDownFormat is called then check the markdown text`() {
        // arrange
        val expected = "# \uD83C\uDF7F\uD83D\uDC39 Analysis -> moduleName\n\n" +
                "| Dependency    | Rule           | Rule Description           | Result         |\n" +
                "| ------------- |:--------------:|:--------------:|:--------------:|\n" +
                "| dependency  | Rule2  | Rule description | Failed ❌|\n"

        val input = ReportData(
            analysisTable = listOf(
                AnalysisTableItemData(
                    "dependency",
                    "Rule2",
                    "Rule description",
                    AnalysisTableResultEnumData.FAILED
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
