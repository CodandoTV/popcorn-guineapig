package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.DependencyFactory
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.domain.usecases.GenerateReportUseCase
import com.github.codandotv.popcorn.presentation.PopcornTaskHelper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Input
import kotlin.reflect.KClass

open class PopcornTask : DefaultTask() {

    private lateinit var checkArcUseCase: CheckArchitectureUseCase
    private lateinit var generateReportUseCase: GenerateReportUseCase

    @Input
    lateinit var configuration: PopcornConfiguration

    @Input
    lateinit var skippedRules: List<KClass<*>>

    private var _errorReportEnabled: Boolean = false

    fun start(
        errorReportEnabled: Boolean,
        dependencyFactory: DependencyFactory
    ) {
        _errorReportEnabled = errorReportEnabled
        checkArcUseCase = dependencyFactory.provideCheckArchitectureUseCase()
        generateReportUseCase = dependencyFactory.provideGenerateReportUseCase()
    }

    @TaskAction
    fun process() {
        PopcornTaskHelper(
            checkArcUseCase = checkArcUseCase,
            generateReportUseCase = generateReportUseCase,
            logger = logger,
            groupName = configuration.project.groupName,
        ).evaluate(
            gradleProject = project,
            projectType = configuration.project.type,
            rules = configuration.rules,
            skippedRules = skippedRules,
            errorReportEnabled = _errorReportEnabled,
        )
    }
}
