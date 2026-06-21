package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.ServiceLocator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

public open class InstallPopcornSkillTask : DefaultTask() {

    private val repository by lazy { ServiceLocator.repository }

    @get:Input
    @get:Optional
    public var skillName: String = "setup-popcorn-plugin"

    private var outputDir: String = ".opencode/skills"

    internal fun configure(outputDir: String) {
        this.outputDir = outputDir
    }

    @TaskAction
    public fun install() {
        repository.installSkill(
            projectDir = project.projectDir.absolutePath,
            skillOutputDir = outputDir,
            skillName = skillName,
        )
    }
}
