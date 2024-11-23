package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableItemDto
import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
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
        assertEquals("| chuck-norris  | DoNotWith  | Passed ✅|\n", result)
    }

}