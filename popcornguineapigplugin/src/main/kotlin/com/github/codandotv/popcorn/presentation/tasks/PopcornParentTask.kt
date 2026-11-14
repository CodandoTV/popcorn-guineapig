package com.github.codandotv.popcorn.presentation.tasks

import com.github.codandotv.popcorn.ServiceLocator
import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.domain.input.configurationNames
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.rules.PopcornGuineaPigRule
import com.github.codandotv.popcorn.domain.usecases.check.AnalyseArchitectureUseCase
import com.github.codandotv.popcorn.presentation.ext.internalProjectDependencies
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerWarn
import com.github.codandotv.popcorn.presentation.ext.toPopcornGPLogger
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option

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

    private var _selectedModules: String? = null

    @get:Input
    @get:Optional
    public val selectedModules: String?
        get() = _selectedModules

    @Option(
        option = "modules",
        description = "Comma-separated Gradle project paths to evaluate. Omit to evaluate all modules.",
    )
    public fun setSelectedModules(value: String): Unit {
        _selectedModules = value
    }

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
        val requestedModules = parseSelectedModules(selectedModules)
        val availableProjectPaths = gradleProjects.mapTo(mutableSetOf()) { it.path }
        val unknownModules = unknownSelectedModules(requestedModules, availableProjectPaths)

        if (unknownModules.isNotEmpty()) {
            error(
                "Unknown Popcorn module path(s): ${unknownModules.sorted().joinToString(", ")}. " +
                        "Use Gradle project paths such as :app or :core:network."
            )
        }

        val projectsToEvaluate = if (requestedModules.isEmpty()) {
            gradleProjects
        } else {
            gradleProjects.filter { it.path in requestedModules }
        }

        logger.popcornLoggerLifecycle(
            if (requestedModules.isEmpty()) {
                "Evaluating all ${projectsToEvaluate.size} modules"
            } else {
                "Evaluating ${projectsToEvaluate.size} of ${gradleProjects.size} modules: " +
                        requestedModules.joinToString(", ")
            }
        )

        val evaluatedProjectPaths = mutableSetOf<String>()
        val targetModules = buildTargetModules(projectsToEvaluate, evaluatedProjectPaths)

        val skippedModules = skippedSelectedModules(requestedModules, evaluatedProjectPaths)
        if (skippedModules.isNotEmpty()) {
            logger.popcornLoggerWarn(
                "Selected module(s) without matching Popcorn rules were skipped: " +
                        skippedModules.sorted().joinToString(", ") +
                        ". Add a PopcornChildConfiguration whose moduleNameRegex matches them, " +
                        "or remove them from --modules."
            )
        }

        analyseArchitectureUseCase.execute(
            modules = targetModules,
            errorReportPath = if (_errorReportEnabled) _reportPath else null
        )
    }

    private fun buildTargetModules(
        projectsToEvaluate: List<Project>,
        evaluatedProjectPaths: MutableSet<String>,
    ): List<TargetModule> {
        val projectsByPath = projectsToEvaluate.associateBy { it.path }
        val rulesByProjectPath = resolveRulesByProjectPath(
            children = children,
            projectPaths = projectsToEvaluate.map { it.path },
        )
        evaluatedProjectPaths.addAll(rulesByProjectPath.keys)

        val configurationNames = type.configurationNames()
        return rulesByProjectPath.map { (modulePath, rules) ->
            val gradleMatchedProject = projectsByPath.getValue(modulePath)
            TargetModule(
                modulePath = modulePath,
                moduleName = gradleMatchedProject.name,
                internalDependencies = gradleMatchedProject.internalProjectDependencies(
                    configurationNames = configurationNames,
                    groupName = _groupName,
                ),
                rules = rules,
            )
        }
    }
}

internal fun resolveRulesByProjectPath(
    children: List<PopcornChildConfiguration>,
    projectPaths: List<String>,
): Map<String, List<PopcornGuineaPigRule>> {
    val rulesByProjectPath = linkedMapOf<String, MutableList<PopcornGuineaPigRule>>()

    children.forEach { child ->
        val moduleNameRegex = child.moduleNameRegex.toRegex()
        projectPaths
            .filter { moduleNameRegex.matches(it) }
            .forEach { projectPath ->
                rulesByProjectPath
                    .getOrPut(projectPath) { mutableListOf() }
                    .addAll(child.rules)
            }
    }

    return rulesByProjectPath.mapValues { (_, rules) -> rules.distinct() }
}
