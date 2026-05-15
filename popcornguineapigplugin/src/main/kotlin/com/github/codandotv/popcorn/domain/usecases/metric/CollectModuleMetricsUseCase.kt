package com.github.codandotv.popcorn.domain.usecases.metric

import com.github.codandotv.popcorn.domain.models.ModuleMetric
import com.github.codandotv.popcorn.domain.models.TargetModule

internal interface CollectModuleMetricsUseCase {

    operator fun invoke(modules: List<TargetModule>): List<ModuleMetric>
}

internal class CollectModuleMetricsUseCaseImpl : CollectModuleMetricsUseCase {
    override fun invoke(modules: List<TargetModule>): List<ModuleMetric> {
        val moduleMetrics = mutableListOf<ModuleMetric>()

        modules.forEach { currentModule ->
            var fanIn = 0

            modules.forEach { module ->
                val isCurrentModuleDependent = module.internalDependencies.any { internalDep ->
                    internalDep.moduleName.equals(currentModule.moduleName, ignoreCase = true)
                }
                if (isCurrentModuleDependent) {
                    fanIn++
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
                    instability = instability
                )
            )
        }

        return moduleMetrics
    }
}
