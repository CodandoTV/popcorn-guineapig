package com.github.codandotv.popcorn.domain.usecases.metric

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.ModuleMetric
import com.github.codandotv.popcorn.domain.models.TargetModule

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

private fun InternalDependenciesMetadata.references(module: TargetModule): Boolean {
    val dependencyPath = modulePath
    return if (dependencyPath != null) {
        dependencyPath == module.modulePath
    } else {
        moduleName.equals(module.moduleName, ignoreCase = true)
    }
}
