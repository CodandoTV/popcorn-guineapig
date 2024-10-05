package com.gabrielbmoro.popcorn.domain.usecases

import com.gabrielbmoro.popcorn.domain.ArchitectureRuleVisitor
import com.gabrielbmoro.popcorn.domain.entity.*
import com.gabrielbmoro.popcorn.domain.entity.InternalDependenciesMetadata
import com.gabrielbmoro.popcorn.domain.entity.TargetModule
import junit.framework.TestCase.assertNull
import org.junit.Test
import kotlin.test.assertNotNull

class ArchitectureRuleVisitorTest {

    @Test
    fun `Given a data layer + invalid relation when do not with rule is checked then fails`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "data",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "resources"),
                )
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )

        // Act
        val result = architectureRuleVisitor.doForDoNotWithRule(
            notWithRule = PopcornDoNotWithRule(
                notWith = listOf("resources"),
                target = "data"
            )
        )

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `Given a data layer + invalid relation + regex when do not with rule is checked then fails`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "user-data",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "user-presentation"),
                )
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "user-presentation"),
            )
        )

        // Act
        val result = architectureRuleVisitor.doForDoNotWithRule(
            notWithRule = PopcornDoNotWithRule(
                notWith = listOf("[a-z]+-presentation"),
                target = "[a-z]+-data"
            )
        )

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `Given a data layer + valid relation when do not with rule is checked then passes`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "data",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain")
                ),
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain")
            )
        )

        // Act
        val result = architectureRuleVisitor.doForDoNotWithRule(
            notWithRule = PopcornDoNotWithRule(
                notWith = listOf("resources"),
                target = "data"
            )
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `Given a domain layer + no relationship when no relationship rule is checked then passes`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "domain",
                internalDependencies = emptyList(),
            ),
            emptyList()
        )

        // Act
        val result = architectureRuleVisitor.doNoRelationshipRule(
            noRelationShipRule = PopcornNoRelationShipRule(
                target = "domain"
            )
        )

        assertNull(result)
    }

    @Test
    fun `Given a domain layer + some relationship when no relationship rule is checked then fails`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "domain",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(
                        group = null,
                        moduleName = "domain"
                    )
                ),
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(
                    group = null,
                    moduleName = "domain"
                )
            ),
        )

        // Act
        val result = architectureRuleVisitor.doNoRelationshipRule(
            PopcornNoRelationShipRule(
                target = "domain"
            )
        )

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `Given a presentation layer + some relations when just with rule is checked then passes`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "presentation",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "resources"),
                )
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )

        // Act
        val result = architectureRuleVisitor.doJustWithRule(
            PopcornJustWithRule(
                target = "presentation",
                with = listOf("domain", "resources")
            )
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `Given a presentation layer + some relations + 2 domains when just with rule is checked then passes`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "presentation",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, "domain"),
                    InternalDependenciesMetadata(group = null, "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "resources"),
                )
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )

        // Act
        val result = architectureRuleVisitor.doJustWithRule(
            PopcornJustWithRule(
                target = "presentation",
                with = listOf("domain", "domain", "resources")
            )
        )

        // Assert
        assertNull(result)
    }

    @Test
    fun `Given a presentation layer + missing relation when just with rule is checked then fails`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "presentation",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "resources"),
                )
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )

        // Act
        val result = architectureRuleVisitor.doJustWithRule(
            PopcornJustWithRule(
                target = "presentation",
                with = listOf("domain")
            )
        )

        // Assert
        assertNotNull(result)
    }

    @Test
    fun `Given a presentation layer + missing relation + regex rule when just with rule is checked then fails`() {
        // Arrange
        val architectureRuleVisitor = ArchitectureRuleVisitor(
            targetModule = TargetModule(
                moduleName = "referral-presentation",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "resources"),
                )
            ),
            sortedInternalProjectDependencies = listOf(
                InternalDependenciesMetadata(group = null, moduleName = "domain"),
                InternalDependenciesMetadata(group = null, moduleName = "resources"),
            )
        )

        // Act
        val result = architectureRuleVisitor.doJustWithRule(
            PopcornJustWithRule(
                target = "[a-z]*-presentation",
                with = listOf("domain")
            )
        )

        // Assert
        assertNotNull(result)
    }
}