package com.github.codandotv.popcorn.data

import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SkillDataSourceTest {

    private val skillDataSource = SkillDataSource()

    @Test
    fun `Given valid parameters when installSkill is called then writes SKILL md file to target directory`() {
        val tempDir = createTempDir()
        val result = skillDataSource.installSkill(
            projectDir = tempDir.absolutePath,
            skillOutputDir = ".opencode/skills",
            skillName = "setup-popcorn-plugin",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )

        assertTrue(result.isSuccess)
        val expectedFile = File(tempDir, ".opencode/skills/setup-popcorn-plugin/SKILL.md")
        assertTrue(expectedFile.exists())
        assertTrue(expectedFile.readText().isNotEmpty())
        assertTrue(expectedFile.readText().contains("name: setup-popcorn-plugin"))
    }

    @Test
    fun `Given existing file with same content when installSkill is called then skips and does not overwrite`() {
        val tempDir = createTempDir()
        val firstResult = skillDataSource.installSkill(
            projectDir = tempDir.absolutePath,
            skillOutputDir = ".opencode/skills",
            skillName = "setup-popcorn-plugin",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )
        assertTrue(firstResult.isSuccess)
        val skillFile = File(tempDir, ".opencode/skills/setup-popcorn-plugin/SKILL.md")
        val firstModified = skillFile.lastModified()

        val secondResult = skillDataSource.installSkill(
            projectDir = tempDir.absolutePath,
            skillOutputDir = ".opencode/skills",
            skillName = "setup-popcorn-plugin",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )

        assertTrue(secondResult.isSuccess)
        assertEquals(firstModified, skillFile.lastModified())
    }

    @Test
    fun `Given existing file with different content when installSkill is called then overwrites`() {
        val tempDir = createTempDir()
        val skillDir = File(tempDir, ".opencode/skills/setup-popcorn-plugin")
        skillDir.mkdirs()
        val skillFile = File(skillDir, "SKILL.md")
        skillFile.writeText("stale content")
        val staleModified = skillFile.lastModified()

        Thread.sleep(10)

        val result = skillDataSource.installSkill(
            projectDir = tempDir.absolutePath,
            skillOutputDir = ".opencode/skills",
            skillName = "setup-popcorn-plugin",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )

        assertTrue(result.isSuccess)
        assertTrue(skillFile.lastModified() > staleModified)
        assertTrue(skillFile.readText().contains("name: setup-popcorn-plugin"))
    }

    @Test
    fun `Given missing resource when installSkill is called then returns failure`() {
        val tempDir = createTempDir()
        val result = skillDataSource.installSkill(
            projectDir = tempDir.absolutePath,
            skillOutputDir = ".opencode/skills",
            skillName = "nonexistent-skill",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun `Given custom output directory when installSkill is called then writes to specified path`() {
        val tempDir = createTempDir()
        val result = skillDataSource.installSkill(
            projectDir = tempDir.absolutePath,
            skillOutputDir = ".cursor/skills",
            skillName = "setup-popcorn-plugin",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )

        assertTrue(result.isSuccess)
        val expectedFile = File(tempDir, ".cursor/skills/setup-popcorn-plugin/SKILL.md")
        assertTrue(expectedFile.exists())
    }

    @Test
    fun `Given project dir without skills parent when installSkill is called then creates intermediate directories`() {
        val tempDir = createTempDir()
        val nestedDir = File(tempDir, "nested/dir")

        val result = skillDataSource.installSkill(
            projectDir = nestedDir.absolutePath,
            skillOutputDir = ".opencode/skills",
            skillName = "setup-popcorn-plugin",
            classLoader = SkillDataSourceTest::class.java.classLoader,
        )

        assertTrue(result.isSuccess)
        val expectedFile = File(nestedDir, ".opencode/skills/setup-popcorn-plugin/SKILL.md")
        assertTrue(expectedFile.exists())
    }

    private fun createTempDir(): File {
        val dir = File(System.getProperty("java.io.tmpdir"), "popcorn-test-${System.nanoTime()}")
        if (dir.exists()) {
            dir.deleteRecursively()
        }
        dir.mkdirs()
        return dir
    }
}
