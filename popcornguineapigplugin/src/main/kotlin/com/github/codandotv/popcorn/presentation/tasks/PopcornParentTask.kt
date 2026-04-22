package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.DependencyFactory
import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.presentation.PopcornTaskHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public open class PopcornParentTask : DefaultTask() {

    private lateinit var checkArcUseCase: CheckArchitectureUseCase
    private lateinit var generateReportUseCase: GenerateReportUseCase

    @Input
    public lateinit var children: List<PopcornChildConfiguration>

    @Input
    public lateinit var type: ProjectType

    private var _errorReportEnabled: Boolean = false
    private var _groupName: String? = null

    private var _reportPath: String = ""

    internal fun start(
        reportPath: String?,
        groupName: String?,
        errorReportEnabled: Boolean,
        dependencyFactory: DependencyFactory
    ) {
        _reportPath = reportPath.orEmpty()
        _groupName = groupName
        _errorReportEnabled = errorReportEnabled
        checkArcUseCase = dependencyFactory.provideCheckArchitectureUseCase()
        generateReportUseCase = dependencyFactory.provideGenerateReportUseCase()
    }

    @TaskAction
    public fun process() {
        val popcornTaskHelper = PopcornTaskHelper(
            checkArcUseCase = checkArcUseCase,
            generateReportUseCase = generateReportUseCase,
            logger = logger,
            groupName = _groupName,
            reportPath = _reportPath,
        )

        val gradleProjects = project.allprojects.toList()

        children.forEach { child ->
            gradleProjects.filter { gradleProject ->
                child.moduleNameRegex.toRegex().matches(gradleProject.path)
            }.forEach { gradleMatchedProject ->
                popcornTaskHelper.evaluate(
                    gradleProject = gradleMatchedProject,
                    projectType = type,
                    rules = child.rules,
                    skippedRules = emptyList(),
                    errorReportEnabled = _errorReportEnabled,
                )
            }
        }
    }
}
