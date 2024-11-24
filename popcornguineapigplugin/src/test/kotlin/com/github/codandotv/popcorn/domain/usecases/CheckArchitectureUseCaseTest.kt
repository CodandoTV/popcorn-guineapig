package com.github.codandotv.popcorn.domain.usecases

import com.github.codandotv.popcorn.domain.output.CheckResult
import com.github.codandotv.popcorn.domain.metadata.TargetModule
import com.github.codandotv.popcorn.domain.metadata.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.rules.DoNotWithRule
import com.github.codandotv.popcorn.domain.rules.JustWithRule
import com.github.codandotv.popcorn.domain.rules.NoDependencyRule
import com.github.codandotv.popcorn.fakes.fakePopcornConfiguration
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
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(DoNotWithRule(notWith = listOf("resources")))
                }
            )
        )

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
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(DoNotWithRule(listOf("[a-z]+-presentation")))
                }
            )
        )

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
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(DoNotWithRule(notWith = listOf("resources")))
                }
            )
        )

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a domain layer + no relationship when no relationship rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "domain",
            internalDependencies = emptyList(),
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(NoDependencyRule())
                }
            )
        )

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
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(NoDependencyRule())
                }
            )
        )

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
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    JustWithRule(
                        justWith = listOf("domain", "resources")
                    )
                }
            )
        )

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
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(JustWithRule(justWith = listOf("domain", "resources")))
                }
            )
        )

        // Assert
        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + some relations (not sorted) when just with rule is checked then passes`() {
        // Arrange
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources")
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(JustWithRule(justWith = listOf("resources", "domain")))
                }
            )
        )

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
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(JustWithRule(justWith = listOf("domain")))
                }
            )
        )

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
            )
        )

        // Act
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.toMutableList().apply {
                    add(JustWithRule(listOf("domain")))
                }
            )
        )

        // Assert
        assertIs<CheckResult.Failure>(result)
    }
}
