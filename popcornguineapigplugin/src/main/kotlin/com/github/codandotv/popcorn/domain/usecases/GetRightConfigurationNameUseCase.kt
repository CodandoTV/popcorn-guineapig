package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.input.ProjectType

public interface GetRightConfigurationNameUseCase {
    fun execute(projectType: ProjectType): String
}

internal class GetRightConfigurationNameUseCaseImpl : GetRightConfigurationNameUseCase {
    override fun execute(projectType: ProjectType) = when (projectType) {
        ProjectType.KMP -> "commonMainImplementation"
        else -> "implementation"
    }
}
