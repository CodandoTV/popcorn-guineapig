package com.github.codandotv.popcorn.domain.input

import org.junit.Test
import kotlin.test.assertEquals

class ProjectTypeTest {
    @Test
    fun `Given a java project when the get right configuration is invoked then check if it is right`() {
        val result = ProjectType.JAVA.configurationName()

        assertEquals("implementation", result)
    }

    @Test
    fun `Given a kmp project when the get right configuration is invoked then check if it is right`() {
        val result = ProjectType.KMP.configurationName()

        assertEquals("commonMainImplementation", result)
    }

    @Test
    fun `Given an android project when the get right configuration is invoked then check if it is right`() {
        val result = ProjectType.ANDROID.configurationName()

        assertEquals("implementation", result)
    }
}
