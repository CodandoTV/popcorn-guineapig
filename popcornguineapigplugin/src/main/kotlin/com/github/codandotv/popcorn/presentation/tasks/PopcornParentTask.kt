package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.ServiceLocator
import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.input.configurationNames
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.usecases.check.AnalyseArchitectureUseCase
import com.github.codandotv.popcorn.presentation.ext.internalProjectDependencies
import com.github.codandotv.popcorn.presentation.ext.toPopcornGPLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public open class PopcornParentTask : DefaultTask() {

    private val analyseArchitectureUseCase: AnalyseArchitectureUseCase by lazy {
        ServiceLocator.provideAnalyseArchitectureUseCase(
            logger = logger.toPopcornGPLogger()
        )
    }

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
    ) {
        _reportPath = reportPath.orEmpty()
        _groupName = groupName
        _errorReportEnabled = errorReportEnabled
    }

    @TaskAction
    public fun process() {
        val gradleProjects = project.allprojects.toList()

        val targetModules = mutableListOf<TargetModule>()
        children.forEach { child ->
            gradleProjects.filter { gradleProject ->
                child.moduleNameRegex.toRegex().matches(gradleProject.path)
            }.forEach { gradleMatchedProject ->
                val internalProjectDependencies = gradleMatchedProject.internalProjectDependencies(
                    configurationNames = type.configurationNames(),
                    groupName = _groupName
                )
                targetModules.add(
                    TargetModule(
                        moduleName = gradleMatchedProject.name,
                        internalDependencies = internalProjectDependencies,
                        rules = child.rules
                    )
                )
            }
        }

        analyseArchitectureUseCase.execute(
            modules = targetModules,
            errorReportPath = if (_errorReportEnabled) _reportPath else null
        )
    }
}
