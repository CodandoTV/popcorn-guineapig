package com.gabrielbmoro.popcorn.domain.usecases

import com.gabrielbmoro.popcorn.domain.input.ProjectType


internal class GetRightConfigurationNameUseCase {

    fun execute(projectType: ProjectType) = when (projectType) {
        ProjectType.KMP -> "commonMainImplementation"
        else -> "implementation"
    }
}
