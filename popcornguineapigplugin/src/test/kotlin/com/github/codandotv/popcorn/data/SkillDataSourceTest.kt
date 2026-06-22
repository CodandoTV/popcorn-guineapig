package com.github.codandotv.popcorn.data

import org.junit.Test
import java.io.File
import kotlin.test.assertFails
import kotlin.test.assertTrue

class SkillDataSourceTest {

    private val skillDataSource = SkillDataSource(
        classLoader = SkillDataSourceTest::class.java.classLoader,
    )

    @Test
    fun `Given valid parameters when installSkill is called then writes SKILL md file to target directory`() {
        val tempDir = createTempDir()
        skillDataSource.installSkill(
            skillOutputDir = File(tempDir, ".opencode/skills").absolutePath,
            skillName = "setup-popcorn-plugin",
        )

        val expectedFile = File(tempDir, ".opencode/skills/setup-popcorn-plugin/SKILL.md")
        assertTrue(expectedFile.exists())
        assertTrue(expectedFile.readText().isNotEmpty())
        assertTrue(expectedFile.readText().contains("name: setup-popcorn-plugin"))
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

        skillDataSource.installSkill(
            skillOutputDir = File(tempDir, ".opencode/skills").absolutePath,
            skillName = "setup-popcorn-plugin",
        )

        assertTrue(skillFile.lastModified() > staleModified)
        assertTrue(skillFile.readText().contains("name: setup-popcorn-plugin"))
    }

    @Test
    fun `Given missing resource when installSkill is called then throws`() {
        val tempDir = createTempDir()
        assertFails {
            skillDataSource.installSkill(
                skillOutputDir = File(tempDir, ".opencode/skills").absolutePath,
                skillName = "nonexistent-skill",
            )
        }
    }

    @Test
    fun `Given custom output directory when installSkill is called then writes to specified path`() {
        val tempDir = createTempDir()
        skillDataSource.installSkill(
            skillOutputDir = File(tempDir, ".cursor/skills").absolutePath,
            skillName = "setup-popcorn-plugin",
        )

        val expectedFile = File(tempDir, ".cursor/skills/setup-popcorn-plugin/SKILL.md")
        assertTrue(expectedFile.exists())
    }

    @Test
    fun `Given project dir without skills parent when installSkill is called then creates intermediate directories`() {
        val tempDir = createTempDir()
        val nestedDir = File(tempDir, "nested/dir")

        skillDataSource.installSkill(
            skillOutputDir = File(nestedDir, ".opencode/skills").absolutePath,
            skillName = "setup-popcorn-plugin",
        )

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
