package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.ProjectType

internal class GetRightConfigurationNameUseCase {

    fun execute(projectType: ProjectType) = when (projectType) {
        ProjectType.KMP -> "commonMainImplementation"
        else -> "implementation"
    }
}