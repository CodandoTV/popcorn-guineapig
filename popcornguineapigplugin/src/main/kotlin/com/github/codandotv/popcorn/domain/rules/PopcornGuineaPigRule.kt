package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.output.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata

interface PopcornGuineaPigRule {

    fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError?
}
