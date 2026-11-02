package com.github.codandotv.popcorn.presentation.tasks

private const val PROJECT_PATH_SEPARATOR = ':'
private const val MODULE_SEPARATOR = ','

internal fun parseSelectedModules(rawSelection: String?): Set<String> {
    return rawSelection
        .orEmpty()
        .split(MODULE_SEPARATOR)
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { normalizeProjectPath(it) }
        .toSet()
}

internal fun normalizeProjectPath(projectPath: String): String {
    return if (projectPath.startsWith(PROJECT_PATH_SEPARATOR)) {
        projectPath
    } else {
        PROJECT_PATH_SEPARATOR + projectPath
    }
}

internal fun unknownSelectedModules(
    selected: Set<String>,
    available: Set<String>,
): Set<String> {
    return selected - available
}

internal fun skippedSelectedModules(
    selected: Set<String>,
    evaluated: Set<String>,
): Set<String> {
    return selected - evaluated
}
