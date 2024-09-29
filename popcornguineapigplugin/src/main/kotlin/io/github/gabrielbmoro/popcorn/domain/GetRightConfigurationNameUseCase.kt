package io.github.gabrielbmoro.popcorn.domain

import io.github.gabrielbmoro.popcorn.domain.entity.ProjectType

class GetRightConfigurationNameUseCase {

    fun execute(projectType: ProjectType) = when (projectType) {
        ProjectType.KMP -> "commonImplementation"
        else -> "implementation"
    }
}