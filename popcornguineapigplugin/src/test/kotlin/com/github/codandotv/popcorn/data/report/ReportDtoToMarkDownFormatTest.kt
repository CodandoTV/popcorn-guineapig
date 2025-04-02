package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.data.dto.AnalysisTableResultEnumDto
import org.junit.Test
import kotlin.test.assertEquals

class ReportDtoToMarkDownFormatTest {
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
}
