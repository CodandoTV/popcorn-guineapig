package com.github.codandotv.popcorn.fakes

import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.usecases.check.CheckArchitectureUseCase

internal class FakeCheckArchitectureUseCase(
    internal val modulesResults: Map<String, CheckResult>,
) : CheckArchitectureUseCase {
    override fun execute(targetModule: TargetModule): CheckResult {
        return modulesResults[targetModule.moduleName]
            ?: CheckResult.Success
    }
}
