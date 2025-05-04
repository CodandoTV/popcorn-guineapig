package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.DependencyFactory
import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.tasks.PopcornParentTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class PopcornGpParentPlugin : Plugin<Project> {

    private lateinit var dependencyFactory: DependencyFactory

    override fun apply(target: Project) {
        dependencyFactory = DependencyFactory(
            reportPath = target.project.layout.buildDirectory.asFile.get().path
        )

        val extension = target.extensions.create(
            "popcornGuineapigParentConfig",
            PopcornGpParentPluginExtension::class.java
        )

        target.tasks.register<PopcornParentTask>("popcornParent") {
            children = extension.children
                ?: error("It is required to specify some configuration")

            type = extension.type
                ?: error("It is required to specify the project type")

            logger.popcornLoggerLifecycle(
                "Register popcornParent at ${project.name}: " +
                        "configuration ${type.name}"
            )

            doFirst {
                val errorReportEnabled = project.hasProperty("errorReportEnabled")

                logger.popcornLoggerLifecycle(
                    "Running popcornParent at ${project.name}: " +
                            "configuration ${type.name}, " +
                            "errorReportEnabled $errorReportEnabled"
                )
                start(
                    groupName = extension.groupName,
                    errorReportEnabled = errorReportEnabled,
                    dependencyFactory = dependencyFactory
                )

                logger.popcornLoggerLifecycle("Start checking ${project.name} module")
            }

            doLast {
                logger.popcornLoggerLifecycle("Finishing the analysis over ${project.name} module")
            }
        }
    }
}

open class PopcornGpParentPluginExtension {
    var type: ProjectType? = null
    var children: List<PopcornChildConfiguration>? = null
    var groupName: String? = null
}
