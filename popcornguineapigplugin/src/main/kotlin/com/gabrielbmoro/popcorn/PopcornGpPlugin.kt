package com.gabrielbmoro.popcorn

import com.gabrielbmoro.popcorn.domain.entity.PopcornConfiguration
import com.gabrielbmoro.popcorn.presentation.tasks.PopcornTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register
import kotlin.reflect.KClass

class PopcornGpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("popcornGuineapigConfig", PopcornGpPluginExtension::class.java)

        target.tasks.register<PopcornTask>("popcorn") {
            configuration = extension.configuration
                ?: throw IllegalStateException("It is required to specify some configuration")
            skippedRules = extension.skippedRules ?: emptyList()
        }
    }
}

open class PopcornGpPluginExtension {
    var configuration: PopcornConfiguration? = null
    var skippedRules: List<KClass<*>>? = null
}