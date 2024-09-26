package com.gabrielbmoro.popcorn.domain

import com.gabrielbmoro.popcorn.domain.entity.ProjectType

class GetRightConfigurationNameUseCase {

    fun execute(projectType: ProjectType) = when (projectType) {
        ProjectType.KMP -> "commonImplementation"
        else -> "implementation"
    }
}