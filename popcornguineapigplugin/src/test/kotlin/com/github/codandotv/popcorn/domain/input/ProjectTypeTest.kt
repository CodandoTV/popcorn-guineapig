package com.github.codandotv.popcorn.domain.input

import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ProjectTypeTest {
    @Test
    fun `Given a java project when the get right configuration is invoked then check if it is right`() {
        val result = ProjectType.JAVA.configurationNames()

        assertContains(result, "implementation")
    }

    @Test
    fun `Given a kmp project when the get right configuration is invoked then check if it is right`() {
        val result = ProjectType.KMP.configurationNames()

        assertContains(result, "commonMainImplementation")
    }

    @Test
    fun `Given an android project when the get right configuration is invoked then check if it is right`() {
        val result = ProjectType.ANDROID.configurationNames()

        assertContains(result, "implementation")
    }
}
