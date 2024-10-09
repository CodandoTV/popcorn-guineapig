package com.gabrielbmoro.popcorn.domain.rules

import com.gabrielbmoro.popcorn.domain.output.ArchitectureViolationError
import com.gabrielbmoro.popcorn.domain.metadata.InternalDependenciesMetadata

interface PopcornGuineaPigRule {

    fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError?
}
