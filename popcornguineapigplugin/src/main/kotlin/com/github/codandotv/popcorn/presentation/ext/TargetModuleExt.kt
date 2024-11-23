package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.metadata.TargetModule

private const val SEPARATOR = "\n----------------------"

fun TargetModule.logMessage(): String {
    val internalDepsReport = internalDependencies.map { it.moduleName }.reduce { acc, s ->
        "$acc,$s"
    }

    return SEPARATOR + "\nModule name: $moduleName\n" +
            "Internal dependencies: " + internalDepsReport + SEPARATOR
}