package com.github.codandotv.popcorn.presentation.tasks

import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleSelectionTest {

    @Test
    fun `Given no raw selection when parsed then all modules are selected`() {
        assertEquals(emptySet(), parseSelectedModules(null))
        assertEquals(emptySet(), parseSelectedModules(""))
        assertEquals(emptySet(), parseSelectedModules(" , "))
    }

    @Test
    fun `Given comma separated paths when parsed then trims and normalizes them`() {
        assertEquals(
            setOf(":app", ":core:network"),
            parseSelectedModules(" :app, core:network "),
        )
    }

    @Test
    fun `Given duplicate paths when parsed then they are deduplicated`() {
        assertEquals(
            setOf(":app"),
            parseSelectedModules(":app,app,:app"),
        )
    }

    @Test
    fun `Given the root project path when parsed then it is preserved`() {
        assertEquals(
            setOf(":"),
            parseSelectedModules(":"),
        )
    }

    @Test
    fun `Given selected paths missing from the build when validated then they are reported`() {
        assertEquals(
            setOf(":missing"),
            unknownSelectedModules(
                selected = setOf(":app", ":missing"),
                available = setOf(":", ":app"),
            ),
        )
    }

    @Test
    fun `Given selected paths without matching rules when evaluated then they are reported`() {
        assertEquals(
            setOf(":unconfigured"),
            skippedSelectedModules(
                selected = setOf(":app", ":unconfigured"),
                evaluated = setOf(":app"),
            ),
        )
    }
}
