package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.DependencyFactory
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.tasks.PopcornTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import kotlin.reflect.KClass

class PopcornGpPlugin : Plugin<Project> {

    private lateinit var dependencyFactory: DependencyFactory

    override fun apply(target: Project) {
        dependencyFactory = DependencyFactory(
            reportPath = target.project.layout.buildDirectory.asFile.get().path
        )

        val extension = target.extensions.create(
            "popcornGuineapigConfig",
            PopcornGpPluginExtension::class.java
        )

        target.tasks.register<PopcornTask>("popcorn") {
            configuration = extension.configuration
                ?: error("It is required to specify some configuration")
            skippedRules = extension.skippedRules ?: emptyList()
            hasReportEnabled = extension.hasReportEnabled

            logger.popcornLoggerLifecycle(
                "Register popcorn at ${project.name}: " +
                        "configuration ${configuration.project.type.name}, " +
                        "hasReportEnabled $hasReportEnabled"
            )

            doFirst {
                start(dependencyFactory = dependencyFactory)

                logger.popcornLoggerLifecycle("Start checking ${project.name} module")
            }

            doLast {
                logger.popcornLoggerLifecycle("Finishing the analysis over ${project.name} module")
            }
        }
    }
}

open class PopcornGpPluginExtension {
    var configuration: PopcornConfiguration? = null
    var skippedRules: List<KClass<*>>? = null
    var hasReportEnabled: Boolean = false
}
