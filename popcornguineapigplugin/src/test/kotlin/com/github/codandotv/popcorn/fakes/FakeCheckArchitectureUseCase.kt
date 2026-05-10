package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.usecases.CheckArchitectureUseCase

internal class FakeCheckArchitectureUseCase(
    internal val modulesResults: Map<String, CheckResult>,
) : CheckArchitectureUseCase {
    override fun execute(targetModule: TargetModule): CheckResult {
        return modulesResults[targetModule.moduleName]
            ?: CheckResult.Success
    }
}