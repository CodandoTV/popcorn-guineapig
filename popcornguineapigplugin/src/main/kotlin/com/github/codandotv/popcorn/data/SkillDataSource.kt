package com.github.codandotv.popcorn.data

import java.io.File

internal class SkillDataSource(
    private val classLoader: ClassLoader = SkillDataSource::class.java.classLoader,
) {

    fun installSkill(
        skillOutputDir: String,
        skillName: String,
    ) {
        val resourcePath = "skills/$skillName/SKILL.md"
        val resourceStream = classLoader.getResourceAsStream(resourcePath)
            ?: error("Skill resource not found: $resourcePath")

        val content = resourceStream.bufferedReader().use { it.readText() }

        val outputDir = File(skillOutputDir).resolve(skillName)
        if (outputDir.exists().not()) {
            outputDir.mkdirs()
        }

        val skillFile = File(outputDir, "SKILL.md")

        if (skillFile.exists()) {
            skillFile.delete()
        }

        skillFile.createNewFile()
        skillFile.bufferedWriter().use { it.write(content) }
    }
}
