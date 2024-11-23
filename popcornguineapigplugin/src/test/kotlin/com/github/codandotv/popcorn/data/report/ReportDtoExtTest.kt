package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import com.github.codandotv.popcorn.data.dto.HowCanIFixThisItemDto
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
}