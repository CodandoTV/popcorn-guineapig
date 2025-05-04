package com.github.codandotv.popcorn.domain.report

import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.CheckResult
import kotlin.reflect.KClass

data class ReportInfo(
    val targetModule: TargetModule,
    val checkResult: CheckResult,
    val skippedRules: List<KClass<*>>?,
)
