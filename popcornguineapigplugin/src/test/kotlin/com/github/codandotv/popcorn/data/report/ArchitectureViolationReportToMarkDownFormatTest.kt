package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ViolationReportType
import org.junit.Test
import kotlin.test.assertEquals

class ArchitectureViolationReportToMarkDownFormatTest {
    @Test
    fun `Given a failed status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = ViolationReportType.FAILED.toMarkdownStatus()

        // assert
        assertEquals("Failed ❌", status)
    }

    @Test
    fun `Given a passed status when toMarkdownStatus occurs then check the markdown text`() {
        // arrange, act
        val status = ViolationReportType.PASSED.toMarkdownStatus()

        // assert
        assertEquals("Passed ✅", status)
    }
}
