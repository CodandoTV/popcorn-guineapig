package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.DependencyFactory
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.tasks.PopcornTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
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

            logger.popcornLoggerLifecycle(
                "Register popcorn at ${project.name}: " +
                        "configuration ${configuration.project.type.name}"
            )

            doFirst {
                val errorReportEnabled = project.hasProperty("errorReportEnabled")

                logger.popcornLoggerLifecycle(
                    "Running popcorn at ${project.name}: " +
                            "configuration ${configuration.project.type.name}, " +
                            "errorReportEnabled $errorReportEnabled"
                )
                start(
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

@Deprecated(
    message = """
        The support for individual plugins will be removed soon, 
        please replace the current configuration to use popcornGuineaPigParent instead. 
        More information you can check 
        [here](https://codandotv.github.io/popcorn-guineapig/1-getting-started/#22-parent-plugin)
        """,
    level = DeprecationLevel.WARNING,
)
open class PopcornGpPluginExtension {
    var configuration: PopcornConfiguration? = null
    var skippedRules: List<KClass<*>>? = null
}
