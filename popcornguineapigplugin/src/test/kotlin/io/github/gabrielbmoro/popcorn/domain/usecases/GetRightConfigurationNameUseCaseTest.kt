package io.github.gabrielbmoro.popcorn.domain.usecases

import org.junit.Test
import io.github.gabrielbmoro.popcorn.domain.CheckArchitectureUseCase
import io.github.gabrielbmoro.popcorn.domain.GetRightConfigurationNameUseCase
import com.gabrielbmoro.popcorn.domain.entity.*
import io.github.gabrielbmoro.popcorn.domain.entity.ProjectType
import org.junit.Before
import kotlin.test.assertEquals
import kotlin.test.assertIs

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

        assertEquals("commonImplementation", result)
    }

    @Test
    fun `Given an android project when the get right configuration is invoked then check if it is right`() {
        val result = getRightConfigurationNameUseCase.execute(ProjectType.ANDROID)

        assertEquals("implementation", result)
    }
}