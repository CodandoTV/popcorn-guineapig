package com.github.codandotv.popcorn.domain.rules

import com.github.codandotv.popcorn.domain.models.ArchitectureViolationError
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata

public interface PopcornGuineaPigRule {

    public fun check(deps: List<InternalDependenciesMetadata>): ArchitectureViolationError?
}
