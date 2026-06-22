package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.Logger
import com.github.codandotv.popcorn.domain.PopcornGuineapigRepository

internal interface InstallSkillUseCase {
    fun execute(
        skillOutputDir: String,
        skillName: String,
    )
}

internal class InstallSkillUseCaseImpl(
    private val logger: Logger,
    private val repository: PopcornGuineapigRepository,
) : InstallSkillUseCase {
    override fun execute(skillOutputDir: String, skillName: String) {
        runCatching {
            repository.installSkill(
                skillOutputDir = skillOutputDir,
                skillName = skillName,
            )
        }.onSuccess {
            logger.logSuccess("AI Skill generated successfully at $skillOutputDir")
        }.onFailure {
            logger.logError("Something went wrong to generate the AI Skill.")
        }
    }
}
