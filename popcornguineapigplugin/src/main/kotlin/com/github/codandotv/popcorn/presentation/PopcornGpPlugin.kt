package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.data.di.REPORT_PATH_KEY
import com.github.codandotv.popcorn.data.di.dataModule
import com.github.codandotv.popcorn.domain.di.domainModule
import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.tasks.PopcornTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import org.koin.core.KoinApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.reflect.KClass

class PopcornGpPlugin : Plugin<Project> {

    private lateinit var koinApp : KoinApplication

    override fun apply(target: Project) {
        val extension = target.extensions.create("popcornGuineapigConfig", PopcornGpPluginExtension::class.java)
        val platformModule = module {
            single<String>(named(REPORT_PATH_KEY)) {
                target.project.layout.buildDirectory.asFile.get().path
            }
        }

        target.tasks.register<PopcornTask>("popcorn") {
            configuration = extension.configuration
                ?: error("It is required to specify some configuration")
            skippedRules = extension.skippedRules ?: emptyList()
            hasReportEnabled = extension.hasReportEnabled

            logger.popcornLoggerLifecycle(
                "Register popcorn at ${target.name}: " +
                        "configuration ${configuration.project.type.name}, " +
                        "hasReportEnabled $hasReportEnabled"
            )

            doFirst {
                koinApp = KoinApplication.init()
                koinApp.modules(platformModule + domainModule + dataModule)
                start(koin = koinApp.koin)

                logger.popcornLoggerLifecycle("Start checking ${target.name} module")
            }

            doLast {
                logger.popcornLoggerLifecycle("Finishing the analysis over ${target.name} module")

                koinApp.close()
            }
        }
    }
}

open class PopcornGpPluginExtension {
    var configuration: PopcornConfiguration? = null
    var skippedRules: List<KClass<*>>? = null
    var hasReportEnabled: Boolean = false
}
