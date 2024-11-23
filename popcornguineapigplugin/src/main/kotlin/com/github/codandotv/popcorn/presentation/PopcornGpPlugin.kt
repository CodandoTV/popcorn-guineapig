package com.github.codandotv.popcorn.presentation

import com.github.codandotv.popcorn.domain.input.PopcornConfiguration
import com.github.codandotv.popcorn.presentation.ext.popcornLoggerLifecycle
import com.github.codandotv.popcorn.presentation.tasks.PopcornTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import kotlin.reflect.KClass

class PopcornGpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("popcornGuineapigConfig", PopcornGpPluginExtension::class.java)

        target.tasks.register<PopcornTask>("popcorn") {
            configuration = extension.configuration
                ?: error("It is required to specify some configuration")
            skippedRules = extension.skippedRules ?: emptyList()


            logger.popcornLoggerLifecycle("Checking ${target.name} \uD83D\uDC40")

            doFirst {
                logger.popcornLoggerLifecycle("Start checking ${target.name} module")
            }

            doLast {
                logger.popcornLoggerLifecycle("Finishing the analysis over ${target.name} module")
            }
        }
    }
}

open class PopcornGpPluginExtension {
    var configuration: PopcornConfiguration? = null
    var skippedRules: List<KClass<*>>? = null
}
