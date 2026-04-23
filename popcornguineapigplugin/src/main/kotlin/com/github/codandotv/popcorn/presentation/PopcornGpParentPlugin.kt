package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.tasks.PopcornParentTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

public class PopcornGpParentPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val reportPath = target.project.layout.buildDirectory.asFile.get().path

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
                    reportPath = reportPath,
                    groupName = extension.groupName,
                    errorReportEnabled = errorReportEnabled,
                )

                logger.popcornLoggerLifecycle("Start checking ${project.name} module")
            }

            doLast {
                logger.popcornLoggerLifecycle("Finishing the analysis over ${project.name} module")
            }
        }
    }
}

public open class PopcornGpParentPluginExtension {
    public var type: ProjectType? = null
    public var children: List<PopcornChildConfiguration>? = null
    public var groupName: String? = null
}
