package com.github.codandotv.popcorn.domain.usecases.metric

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.ModuleMetric
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.models.identityKey

internal interface CollectModuleMetricsUseCase {

    operator fun invoke(modules: List<TargetModule>): List<ModuleMetric>
}

internal class CollectModuleMetricsUseCaseImpl : CollectModuleMetricsUseCase {
    override fun invoke(modules: List<TargetModule>): List<ModuleMetric> {
        val moduleMetrics = mutableListOf<ModuleMetric>()

        modules.forEach { currentModule ->
            val fanIn = modules.count { module ->
                module.internalDependencies.any { internalDep ->
                    internalDep.references(currentModule)
                }
            }

            val fanOut = currentModule.internalDependencies.count()

            val fanSum = fanIn.plus(fanOut).toFloat()

            val instability = if (fanSum != 0f) {
                fanOut.div(
                    fanSum
                )
            } else {
                0f
            }

            moduleMetrics.add(
                ModuleMetric(
                    name = currentModule.moduleName,
                    fanIn = fanIn,
                    fanOut = fanOut,
                    instability = instability,
                    modulePath = currentModule.modulePath,
                )
            )
        }

        return moduleMetrics
    }
}

/**
 * A project declaration is matched by its project path. A coordinate-only declaration has no path,
 * so it matches the module name, ignoring case (the documented leaf-name fallback in
 * docs/5-metrics-report.md). Both branches compare against the shared `identityKey`.
 */
private fun InternalDependenciesMetadata.references(module: TargetModule): Boolean =
    if (modulePath != null) {
        identityKey == module.modulePath
    } else {
        identityKey == module.moduleName.lowercase()
    }
