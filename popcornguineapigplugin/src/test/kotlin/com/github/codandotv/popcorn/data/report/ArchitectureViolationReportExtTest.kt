@file:Suppress("MaxLineLength", "LongMethod")
package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ArchitectureViolationReportExtTest {
    @Test
    fun `Given an empty list when toMarkdownReport is called then returns empty string`() {
        val reports = emptyList<ArchitectureViolationReport>()
        val result = reports.toMarkdownReport()
        assertEquals("", result)
    }

    @Test
    fun `Given a single module with one failed violation when toMarkdownReport is called then returns expected markdown`() {
        val analysisItem = ViolationReportItem(
            internalDependencyName = ":shared",
            ruleChecked = "DoNotWithRule",
            ruleDescription = "Module cannot depend on :shared",
            result = ViolationReportType.FAILED,
        )
        val report = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(analysisItem),
        )
        val result = listOf(report).toMarkdownReport()
        val expected =
"""# 🍿🐹 Popcorn Guinea Pig Architecture Error Report


## Analysis: :feature-home Module

### Summary
- **Status**: Failed ❌
- **Module**: :feature-home
- **Violations Found**: 1

---

## Violation Details
| # | Module | Dependency | Rule | Description | Status |
| --|--------|------------|------|-------------|--------|
| 1 | :feature-home | :shared | DoNotWithRule | Module cannot depend on :shared | Failed ❌ |
            
-----

"""
        assertEquals(expected, result)
    }

    @Test
    fun `Given a single module with multiple failed violations when toMarkdownReport is called then general status is failed`() {
        val analysisItem1 = ViolationReportItem(
            internalDependencyName = ":shared",
            ruleChecked = "DoNotWithRule",
            ruleDescription = "Module cannot depend on :shared",
            result = ViolationReportType.FAILED,
        )
        val analysisItem2 = ViolationReportItem(
            internalDependencyName = ":network",
            ruleChecked = "DoNotWithRule",
            ruleDescription = "Module cannot depend on :network",
            result = ViolationReportType.FAILED,
        )
        val report = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(analysisItem1, analysisItem2),
        )
        val result = listOf(report).toMarkdownReport()
        val expected =
"""# 🍿🐹 Popcorn Guinea Pig Architecture Error Report


## Analysis: :feature-home Module

### Summary
- **Status**: Failed ❌
- **Module**: :feature-home
- **Violations Found**: 2

---

## Violation Details
| # | Module | Dependency | Rule | Description | Status |
| --|--------|------------|------|-------------|--------|
| 1 | :feature-home | :shared | DoNotWithRule | Module cannot depend on :shared | Failed ❌ |
| 2 | :feature-home | :network | DoNotWithRule | Module cannot depend on :network | Failed ❌ |
            
-----

"""
        assertEquals(expected, result)
    }

    @Test
    fun `Given multiple modules with failed violations when toMarkdownReport is called then each module has its own section`() {
        val homeReport = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(
                ViolationReportItem(
                    internalDependencyName = ":shared",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "Module cannot depend on :shared",
                    result = ViolationReportType.FAILED,
                ),
            ),
        )
        val loginReport = ArchitectureViolationReport(
            modulePath = ":feature-login",
            analysisTable = listOf(
                ViolationReportItem(
                    internalDependencyName = ":network",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "Module cannot depend on :network",
                    result = ViolationReportType.FAILED,
                ),
            ),
        )
        val result = listOf(homeReport, loginReport).toMarkdownReport()
        val expected =
"""# 🍿🐹 Popcorn Guinea Pig Architecture Error Report


## Analysis: :feature-home Module

### Summary
- **Status**: Failed ❌
- **Module**: :feature-home
- **Violations Found**: 1

---

## Violation Details
| # | Module | Dependency | Rule | Description | Status |
| --|--------|------------|------|-------------|--------|
| 1 | :feature-home | :shared | DoNotWithRule | Module cannot depend on :shared | Failed ❌ |
            
-----


## Analysis: :feature-login Module

### Summary
- **Status**: Failed ❌
- **Module**: :feature-login
- **Violations Found**: 1

---

## Violation Details
| # | Module | Dependency | Rule | Description | Status |
| --|--------|------------|------|-------------|--------|
| 1 | :feature-login | :network | DoNotWithRule | Module cannot depend on :network | Failed ❌ |
            
-----

"""
        assertEquals(expected, result)
    }

    @Test
    fun `Given modules sharing a name when toMarkdownReport is called then each path has its own section`() {
        val carDataItem = ViolationReportItem(
            internalDependencyName = ":car:domain",
            ruleChecked = "DoNotWithRule",
            ruleDescription = "Module cannot depend on :car:domain",
            result = ViolationReportType.FAILED,
        )
        val rentDataItem = ViolationReportItem(
            internalDependencyName = ":rent:domain",
            ruleChecked = "DoNotWithRule",
            ruleDescription = "Module cannot depend on :rent:domain",
            result = ViolationReportType.FAILED,
        )
        val reports = listOf(
            ArchitectureViolationReport(modulePath = ":car:data", analysisTable = listOf(carDataItem)),
            ArchitectureViolationReport(modulePath = ":rent:data", analysisTable = listOf(rentDataItem)),
        )

        val result = reports.toMarkdownReport()

        assertEquals(2, result.split("## Analysis:").size - 1)
        assertTrue(result.contains("## Analysis: :car:data Module"))
        assertTrue(result.contains("## Analysis: :rent:data Module"))
    }

    @Test
    fun `Given a rule pattern with a pipe when toMarkdownReport is called then the description stays in one cell`() {
        val report = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(
                ViolationReportItem(
                    internalDependencyName = ":data",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "This module should not depends on [(data|network)]",
                    result = ViolationReportType.FAILED,
                ),
            ),
        )

        val result = listOf(report).toMarkdownReport()

        val row = result.rowStartingWith("| 1 |")
        assertEquals(6, row.cellCount())
        assertContains(row, "| This module should not depends on [(data\\|network)] |")
    }

    @Test
    fun `Given a description with a backtick and a line break when toMarkdownReport is called then both stay in one cell`() {
        val report = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(
                ViolationReportItem(
                    internalDependencyName = ":data",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "Pattern `a|b`\nand more",
                    result = ViolationReportType.FAILED,
                ),
            ),
        )

        val row = listOf(report).toMarkdownReport().rowStartingWith("| 1 |")

        assertEquals(6, row.cellCount())
        assertContains(row, "Pattern \\`a\\|b\\` and more")
    }

    /**
     * Backslashes are escaped before pipes, otherwise a value that already contains `\|` would turn
     * into an escaped backslash followed by a raw pipe and still break the table. The expectation
     * holds three backslashes and a pipe: two escape the backslash, one escapes the pipe.
     */
    @Test
    fun `Given a value with a backslash escaped pipe when escaped then the pipe stays escaped`() {
        assertEquals("a\\\\\\|b", "a\\|b".escapeMarkdownCell())
    }

    @Test
    fun `Given a module analysed twice when toMarkdownReport is called then violations share one summary and one table`() {
        val sharedReport = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(
                ViolationReportItem(
                    internalDependencyName = ":shared",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "Module cannot depend on :shared",
                    result = ViolationReportType.FAILED,
                ),
            ),
        )
        val networkReport = ArchitectureViolationReport(
            modulePath = ":feature-home",
            analysisTable = listOf(
                ViolationReportItem(
                    internalDependencyName = ":network",
                    ruleChecked = "DoNotWithRule",
                    ruleDescription = "Module cannot depend on :network",
                    result = ViolationReportType.FAILED,
                ),
            ),
        )

        val result = listOf(sharedReport, networkReport).toMarkdownReport()

        assertEquals(1, result.split("## Analysis:").size - 1)
        assertEquals(1, result.lines().count { it.startsWith("- **Violations Found**") })
        assertEquals(1, result.lines().count { it.contains("## Violation Details") })
        assertContains(result, "- **Violations Found**: 2")
        assertContains(result, "| 1 | :feature-home | :shared |")
        assertContains(result, "| 2 | :feature-home | :network |")
    }

    private fun String.rowStartingWith(prefix: String): String = lines().first { it.startsWith(prefix) }

    /** Cells of a Markdown table row, splitting on every pipe that is not escaped with a backslash. */
    private fun String.cellCount(): Int = trim()
        .removePrefix("|")
        .removeSuffix("|")
        .split(Regex("(?<!\\\\)\\|"))
        .size
}
