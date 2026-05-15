package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import org.junit.Test
import kotlin.test.assertEquals

class ArchitectureViolationReportToMarkDownTableLineTest {
    @Test
    fun `Given some table item when toMarkdownTableLine occurs then check the markdown text`() {
        // arrange
        val tableItem = ViolationReportItem(
            internalDependencyName = "chuck-norris",
            ruleChecked = "DoNotWith",
            ruleDescription = "DoNotWith desc",
            result = ViolationReportType.PASSED
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
            ViolationReportItem(
                internalDependencyName = "chuck-norris",
                ruleChecked = "DoNotWith",
                ruleDescription = "DoNotWith desc",
                result = ViolationReportType.PASSED
            ),
            ViolationReportItem(
                internalDependencyName = "bruce-lee",
                ruleChecked = "JustWith",
                ruleDescription = "JustWith desc",
                result = ViolationReportType.FAILED
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

        val table = emptyList<ViolationReportItem>()

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

        val input = ArchitectureViolationReport(
            analysisTable = listOf(
                ViolationReportItem(
                    "dependency",
                    "Rule2",
                    "Rule description",
                    ViolationReportType.FAILED
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
