package com.github.codandotv.popcorn.domain.usecases.check

import com.github.codandotv.popcorn.domain.models.CheckResult
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.JustWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import org.junit.Test
import org.junit.Before
import kotlin.test.assertIs

class CheckArchitectureUseCaseTest {

    private lateinit var checkArchitectureUseCase: CheckArchitectureUseCase

    @Before
    fun before() {
        checkArchitectureUseCase = CheckArchitectureUseCaseImpl()
    }

    @Test
    fun `Given a data layer + invalid relation when do not with rule is checked then fails`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            ),
            rules = listOf(DoNotWithRule(notWith = listOf("resources")))
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a data layer + invalid relation + regex when do not with rule is checked then fails`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "user-data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "user-presentation"),
            ),
            rules = listOf(DoNotWithRule(listOf("[a-z]+-presentation"))),
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a data layer + valid relation when do not with rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain")
            ),
            rules = listOf(DoNotWithRule(notWith = listOf("resources")))
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a domain layer + no relationship when no relationship rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "domain",
            rules = listOf(NoDependencyRule()),
            internalDependencies = emptyList(),
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a domain layer + some relationship when no relationship rule is checked then fails`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "domain",
            internalDependencies = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "domain"
                )
            ),
            rules = listOf(NoDependencyRule())
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a presentation layer + some relations when just with rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            ),
            rules = listOf(
                JustWithRule(
                    justWith = listOf("domain", "resources")
                )
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + some relations + 2 domains when just with rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            ),
            rules = listOf(JustWithRule(justWith = listOf("domain", "resources")))
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + some relations (not sorted) when just with rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "presentation",
            rules = listOf(JustWithRule(justWith = listOf("resources", "domain"))),
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources")
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + missing relation when just with rule is checked then fails`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
            ),
            rules = listOf(JustWithRule(justWith = listOf("domain")))
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a presentation layer + missing relation + regex rule when just with rule is checked then fails`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "referral-presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
            ),
            rules = listOf(JustWithRule(listOf("domain")))
        )

        // Act
        val result = checkArchitectureUseCase.execute(targetModule)

        // Assert
        assertIs<CheckResult.Failure>(result)
    }
}
