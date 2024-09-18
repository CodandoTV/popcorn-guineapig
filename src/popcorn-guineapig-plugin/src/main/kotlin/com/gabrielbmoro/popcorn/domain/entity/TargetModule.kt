package com.gabrielbmoro.popcorn.domain.entity

data class TargetModule(
    val moduleName: String,
    val internalDependencies: List<InternalDependenciesMetadata>
) {
    override fun toString(): String {
        val internalDep = internalDependencies.map {
            it.moduleName
        }.reduce { acc, s -> acc.plus(",$s") }

        return "[Mod $moduleName, internalDeps $internalDep]"
    }
}