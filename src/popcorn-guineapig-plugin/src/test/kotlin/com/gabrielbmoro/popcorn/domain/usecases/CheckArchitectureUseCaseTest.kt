package com.gabrielbmoro.popcorn.domain.usecases

import org.junit.Test
import com.gabrielbmoro.popcorn.domain.entity.CheckResult
import com.gabrielbmoro.popcorn.domain.entity.PopcornDoNotWithRule
import com.gabrielbmoro.popcorn.domain.entity.PopcornJustWithRule
import com.gabrielbmoro.popcorn.domain.entity.PopcornNoRelationShipRule
import com.gabrielbmoro.popcorn.domain.entity.TargetModule
import com.gabrielbmoro.popcorn.domain.usecase.CheckArchitectureUseCase
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
            internalDependencies = listOf("domain", "resources"),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    doNotWith = listOf(
                        PopcornDoNotWithRule(
                            regexEnabled = false,
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
    fun `Given a data layer + valid relation when do not with rule is checked then passes`() {
        val targetModule = TargetModule(
            moduleName = "data",
            internalDependencies = listOf("domain"),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    doNotWith = listOf(
                        PopcornDoNotWithRule(
                            regexEnabled = false,
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
                            regexEnabled = false,
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
            internalDependencies = listOf("domain"),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    noRelationship = listOf(
                        PopcornNoRelationShipRule(
                            regexEnabled = false,
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
            internalDependencies = listOf("domain", "resources"),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            regexEnabled = false,
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
            internalDependencies = listOf("domain", "resources"),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            regexEnabled = false,
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
            internalDependencies = listOf("resources", "domain"),
        )
        val result = checkArchitectureUseCase.execute(
            targetModule = targetModule,
            configuration = fakePopcornConfiguration.copy(
                rules = fakePopcornConfiguration.rules.copy(
                    justWith = listOf(
                        PopcornJustWithRule(
                            regexEnabled = false,
                            target = "presentation",
                            with = listOf("domain")
                        )
                    )
                )
            )
        )

        assertIs<CheckResult.Failure>(result)
    }
}