package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.input.ProjectType


internal class GetRightConfigurationNameUseCase {

    fun execute(projectType: ProjectType) = when (projectType) {
        ProjectType.KMP -> "commonMainImplementation"
        else -> "implementation"
    }
}
