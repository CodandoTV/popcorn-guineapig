package com.github.codandotv.popcorn.presentation.ext

import org.gradle.api.GradleException
import org.gradle.util.GradleVersion
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertFailsWith

class GradleCompatibilityTest {

    @Test
    fun `Given Gradle 8_10 when checked then throws a descriptive exception`() {
        val exception = assertFailsWith<GradleException> {
            requireMinimumGradleVersion(GradleVersion.version("8.10"))
        }

        assertContains(exception.message.orEmpty(), "8.11")
    }

    @Test
    fun `Given Gradle 8_11 when checked then it is accepted`() {
        requireMinimumGradleVersion(GradleVersion.version("8.11"))
    }

    @Test
    fun `Given Gradle 8_11_1 when checked then it is accepted`() {
        requireMinimumGradleVersion(GradleVersion.version("8.11.1"))
    }

    @Test
    fun `Given Gradle 9 when checked then it is accepted`() {
        requireMinimumGradleVersion(GradleVersion.version("9.0"))
    }
}
