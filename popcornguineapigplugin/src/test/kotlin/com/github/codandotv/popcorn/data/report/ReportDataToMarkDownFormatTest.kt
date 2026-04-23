package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.report.AnalysisTableResultEnumData
import org.junit.Test
import kotlin.test.assertEquals

class ReportDataToMarkDownFormatTest {
    @Test
    fun `Given a failed status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = AnalysisTableResultEnumData.FAILED.toMarkdownStatus()

        // assert
        assertEquals("Failed ❌", status)
    }

    @Test
    fun `Given a skipped status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = AnalysisTableResultEnumData.SKIPPED.toMarkdownStatus()

        // assert
        assertEquals("Skipped ⚠️", status)
    }

    @Test
    fun `Given a passed status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = AnalysisTableResultEnumData.PASSED.toMarkdownStatus()

        // assert
        assertEquals("Passed ✅", status)
    }
}
