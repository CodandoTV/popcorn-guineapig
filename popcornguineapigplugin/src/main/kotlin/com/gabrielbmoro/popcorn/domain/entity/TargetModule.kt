package com.gabrielbmoro.popcorn.domain.entity

data class TargetModule(
    val moduleName: String,
    val internalDependencies: List<InternalDependenciesMetadata>
) {
    override fun toString(): String {
        val internalDep = internalDependencies.map {
            it.moduleName
        }

        val internalDepText = if(internalDep.isNotEmpty()) {
            internalDep.reduce { acc, s -> acc.plus(",$s") }
        } else "empty"

        return "[Mod $moduleName, internalDeps $internalDepText]"
    }
}