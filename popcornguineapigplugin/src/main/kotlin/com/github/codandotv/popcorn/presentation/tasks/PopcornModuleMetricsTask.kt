package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.ServiceLocator
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.input.configurationNames
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.usecases.metric.GenerateMetricsReportUseCase
import com.github.codandotv.popcorn.presentation.ext.internalProjectDependencies
import com.github.codandotv.popcorn.presentation.ext.toPopcornGPLogger
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

public open class PopcornModuleMetricsTask : DefaultTask() {

    private var _groupName: String? = null
    private var _reportPath: String = ""

    private val generateMetricsReportUseCase: GenerateMetricsReportUseCase by lazy {
        ServiceLocator.provideGenerateMetricsReportUseCase(
            logger = logger.toPopcornGPLogger()
        )
    }

    @Input
    public lateinit var type: ProjectType

    internal fun start(
        reportPath: String,
        groupName: String?,
    ) {
        _reportPath = reportPath
        _groupName = groupName
    }

    @TaskAction
    public fun process() {
        val gradleProjects = project.allprojects.toList()
        val configurationNames = type.configurationNames()
        val modules = gradleProjects.map { gradleMatchedProject ->
            TargetModule(
                moduleName = gradleMatchedProject.name,
                internalDependencies = gradleMatchedProject.internalProjectDependencies(
                    configurationNames = configurationNames,
                    groupName = _groupName,
                ),
                rules = emptyList(),
            )
        }

        generateMetricsReportUseCase.invoke(
            metricsReportPath = _reportPath,
            modules = modules
        )
    }
}
