package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.fakes.FakeLogger
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithCallbacks
import org.junit.Test
import kotlin.test.assertTrue

class InstallSkillUseCaseTest {

    @Test
    fun `Given valid parameters when execute then logs success message`() {
        val logMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(logMessages, errorMessages)

        var installSkillCalled = false
        val fakeRepository = fakePopcornGuineapigRepositoryWithCallbacks(
            onInstallSkillCallback = {
                installSkillCalled = true
            }
        )

        val installSkillUseCase = InstallSkillUseCaseImpl(
            logger = fakeLogger,
            repository = fakeRepository,
        )

        installSkillUseCase.execute(
            skillOutputDir = "/tmp/skills",
            skillName = "setup-popcorn-plugin",
        )

        assertTrue(installSkillCalled)
        assertTrue(logMessages.first().contains("AI Skill generated successfully at /tmp/skills"))
    }

    @Test
    fun `Given repository throws when execute then logs error message`() {
        val logMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()
        val fakeLogger = FakeLogger(logMessages, errorMessages)

        val fakeRepository = fakePopcornGuineapigRepositoryWithCallbacks(
            onInstallSkillCallback = {
                throw RuntimeException("install failure")
            }
        )

        val installSkillUseCase = InstallSkillUseCaseImpl(
            logger = fakeLogger,
            repository = fakeRepository,
        )

        installSkillUseCase.execute(
            skillOutputDir = "/tmp/skills",
            skillName = "setup-popcorn-plugin",
        )

        assertTrue(errorMessages.isNotEmpty())
        assertTrue(errorMessages.first().contains("Something went wrong to generate the AI Skill."))
    }
}
