package com.github.codandotv.popcorn.data.report

import com.github.codandotv.popcorn.domain.models.ModuleMetric

internal fun List<ModuleMetric>.toMetricsReport(): String {
    val reportContent = map {
        "${it.name},${it.fanIn},${it.fanOut},${it.instability}"
    }.reduceOrNull { acc, item ->
        "$acc\n$item"
    }

    return if (reportContent.isNullOrEmpty()) {
        ""
    } else {
        val header = "name,fanIn,fanOut,instability\n"
        header.plus(reportContent)
    }
}
