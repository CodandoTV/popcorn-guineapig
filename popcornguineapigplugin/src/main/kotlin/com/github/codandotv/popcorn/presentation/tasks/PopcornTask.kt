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

@Deprecated(
    message = """
        The support for individual plugins will be removed soon, 
        please replace the current configuration to use popcornGuineaPigParent instead. 
        More information you can check 
        [here](https://codandotv.github.io/popcorn-guineapig/1-getting-started/#22-parent-plugin)
        """,
    level = DeprecationLevel.WARNING,
)
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
