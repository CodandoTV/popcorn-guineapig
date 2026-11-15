package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.domain.input.PopcornChildConfiguration
import com.github.codandotv.popcorn.domain.input.ProjectType
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.ext.requireMinimumGradleVersion
import com.github.codandotv.popcorn.presentation.tasks.InstallPopcornSkillTask
import com.github.codandotv.popcorn.presentation.tasks.PopcornModuleMetricsTask
import com.github.codandotv.popcorn.presentation.tasks.PopcornParentTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

public class PopcornGpParentPlugin : Plugin<Project> {

    @Suppress("DEPRECATION") // groupName stays readable for 3.x and warns at execution time.
    override fun apply(target: Project) {
        requireMinimumGradleVersion()

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

        target.tasks.register<PopcornModuleMetricsTask>("popcornModuleMetrics") {
            type = extension.type
                ?: error("It is required to specify the project type")

            doFirst {
                start(
                    reportPath = reportPath,
                    groupName = extension.groupName,
                )
            }
        }

        target.tasks.register<InstallPopcornSkillTask>("installPopcornSkill") {
            doFirst {
                val dir = extension.skillOutputDir?.path
                    ?: project.findProperty("popcornSkillOutputDir") as? String
                    ?: ".opencode/skills"
                configure(dir)
            }
        }
    }
}

public open class PopcornGpParentPluginExtension {
    public var type: ProjectType? = null
    public var children: List<PopcornChildConfiguration>? = null

    /**
     * Only needed for internal modules declared by coordinates (`com.popcorn:data:1.0`) instead of
     * `project(":data")`: a Gradle project dependency is detected by its type, whatever `group`
     * the target project sets.
     *
     * The value matches whole group segments — `com.popcorn` covers `com.popcorn` and
     * `com.popcorn.data`, never `com.popcornx` — and no longer defaults to the root project name,
     * which turned libraries such as `androidx.core:core-ktx` into internal dependencies in a
     * project rooted at `android`.
     */
    @Deprecated(
        message = "Internal dependencies are detected by their Gradle project type. `groupName` " +
                "only matches internal modules declared by coordinates and will be removed in 4.0.0.",
    )
    public var groupName: String? = null

    public var skillOutputDir: java.io.File? = null
}
