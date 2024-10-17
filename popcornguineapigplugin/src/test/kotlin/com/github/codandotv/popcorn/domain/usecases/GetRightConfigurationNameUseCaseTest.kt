package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.input.ProjectType
import org.junit.Test
import org.junit.Before
import kotlin.test.assertEquals

class GetRightConfigurationNameUseCaseTest {

    private lateinit var getRightConfigurationNameUseCase: GetRightConfigurationNameUseCase

    @Before
    fun before() {
        getRightConfigurationNameUseCase = GetRightConfigurationNameUseCase()
    }

    @Test
    fun `Given a java project when the get right configuration is invoked then check if it is right`() {
        val result = getRightConfigurationNameUseCase.execute(ProjectType.JAVA)

        assertEquals("implementation", result)
    }

    @Test
    fun `Given a kmp project when the get right configuration is invoked then check if it is right`() {
        val result = getRightConfigurationNameUseCase.execute(ProjectType.KMP)

        assertEquals("commonMainImplementation", result)
    }

    @Test
    fun `Given an android project when the get right configuration is invoked then check if it is right`() {
        val result = getRightConfigurationNameUseCase.execute(ProjectType.ANDROID)

        assertEquals("implementation", result)
    }
}
