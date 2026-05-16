@file:Suppress("MaxLineLength", "LongMethod")
package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationReport
import com.github.codandotv.popcorn.domain.models.ViolationReportItem
import com.github.codandotv.popcorn.domain.models.ViolationReportType
import org.junit.Test
import kotlin.test.assertEquals

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
            moduleName = ":feature-home",
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
            moduleName = ":feature-home",
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
            moduleName = ":feature-home",
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
            moduleName = ":feature-login",
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
}
