package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
import com.github.codandotv.popcorn.data.dto.ReportDto
import org.junit.Test
import kotlin.test.assertEquals

class ReportDtoExtTest {

    // region toMarkdownStatus
    @Test
    fun `Given a failed status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = AnalysisTableResultEnumDto.FAILED.toMarkdownStatus()

        // assert
        assertEquals("Failed ❌", status)
    }

    @Test
    fun `Given a skipped status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = AnalysisTableResultEnumDto.SKIPPED.toMarkdownStatus()

        // assert
        assertEquals("Skipped ⚠️", status)
    }

    @Test
    fun `Given a passed status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = AnalysisTableResultEnumDto.PASSED.toMarkdownStatus()

        // assert
        assertEquals("Passed ✅", status)
    }
    // endregion

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
    fun `Given a how to fix section object x when toMarkdownSection is invoked then check markdown result`() {
        // arrange
        val howCanIFixThisItemDto = listOf(
            HowCanIFixThisItemDto(
                "DoNotWith", "You should not depends on x, and, y"
            ),
            HowCanIFixThisItemDto("JustWith", "You should depends only on x")
        )

        // act
        val result = howCanIFixThisItemDto.toMarkdownSection()

        // assert
        assertEquals(
            "- DoNotWith: You should not depends on x, and, y\n" +
                    "- JustWith: You should depends only on x",
            result
        )
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
    fun `Given some list of items when toEnumeratedMarkdownList is called then check the markdown text`() {
        val input = listOf("Chuck Norris", "Bruce lee", "Popo Freitas")

        val result = input.toEnumeratedMarkdownList()

        assertEquals(
            "1. Chuck Norris\n2. Bruce lee\n3. Popo Freitas",
            result
        )
    }

    @Test
    fun `Given some list of items when toSimpleMarkdownList is called then check the markdown text`() {
        val input = listOf("Chuck Norris", "Bruce lee", "Popo Freitas")

        val result = input.toSimpleMarkdownList()

        assertEquals(
            "- Chuck Norris\n- Bruce lee\n- Popo Freitas",
            result
        )
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
            moduleName = "dependency"
        )

        // act
        val result = input.toMarkDownFormat()

        // assert
        assertEquals(expected, result)
    }
}
