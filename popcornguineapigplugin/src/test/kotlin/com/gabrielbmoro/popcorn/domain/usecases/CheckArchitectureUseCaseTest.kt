package com.gabrielbmoro.popcorn.domain.usecases

import com.gabrielbmoro.popcorn.domain.CheckArchitectureUseCase
import com.gabrielbmoro.popcorn.domain.entity.*
import org.junit.Test
import org.junit.Before
import kotlin.test.assertIs

class CheckArchitectureUseCaseTest {

    private lateinit var checkArchitectureUseCase: CheckArchitectureUseCase

    @Before
    fun before() {
        checkArchitectureUseCase = CheckArchitectureUseCase()
    }

    @Test
    fun `Given a data layer + invalid relation when do not with rule is checked then fails`() {
        val targetModule = TargetModule(
            moduleName = "data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    doNotWith = listOf(
                        PopcornDoNotWithRule(
                            notWith = listOf("resources"),
                            target = "data"
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a data layer + invalid relation + regex when do not with rule is checked then fails`() {
        val targetModule = TargetModule(
            moduleName = "user-data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "user-presentation"),
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    doNotWith = listOf(
                        PopcornDoNotWithRule(
                            notWith = listOf("[a-z]+-presentation"),
                            target = "[a-z]+-data"
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a data layer + valid relation when do not with rule is checked then passes`() {
        val targetModule = TargetModule(
            moduleName = "data",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain")
            ),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    doNotWith = listOf(
                        PopcornDoNotWithRule(
                            notWith = listOf("resources"),
                            target = "data"
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a domain layer + no relationship when no relationship rule is checked then passes`() {
        val targetModule = TargetModule(
            moduleName = "domain",
            internalDependencies = emptyList(),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    noRelationship = listOf(
                        PopcornNoRelationShipRule(
                            target = "domain"
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a domain layer + some relationship when no relationship rule is checked then fails`() {
        val targetModule = TargetModule(
            moduleName = "domain",
            internalDependencies = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "domain"
                )
            ),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    noRelationship = listOf(
                        PopcornNoRelationShipRule(
                            target = "domain"
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a presentation layer + some relations when just with rule is checked then passes`() {
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            target = "presentation",
                            with = listOf("domain", "resources")
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + some relations + 2 domains when just with rule is checked then passes`() {
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            target = "presentation",
                            with = listOf("domain", "resources")
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + some relations (not sorted) when just with rule is checked then passes`() {
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources")
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            target = "presentation",
                            with = listOf("resources", "domain")
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Success>(result)
    }

    @Test
    fun `Given a presentation layer + missing relation when just with rule is checked then fails`() {
        val targetModule = TargetModule(
            moduleName = "presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            target = "presentation",
                            with = listOf("domain")
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Failure>(result)
    }

    @Test
    fun `Given a presentation layer + missing relation + regex rule when just with rule is checked then fails`() {
        val targetModule = TargetModule(
            moduleName = "referral-presentation",
            internalDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
            )
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            target = "[a-z]*-presentation",
                            with = listOf("domain")
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Failure>(result)
    }
}