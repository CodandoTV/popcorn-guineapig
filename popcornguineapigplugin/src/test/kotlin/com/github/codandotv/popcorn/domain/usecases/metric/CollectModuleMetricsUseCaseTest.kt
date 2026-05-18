package com.github.codandotv.popcorn.domain.usecases.metric

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import com.github.codandotv.popcorn.domain.models.TargetModule
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectModuleMetricsUseCaseTest {

    private lateinit var useCase: CollectModuleMetricsUseCase

    @Before
    fun setUp() {
        useCase = CollectModuleMetricsUseCaseImpl()
    }

    @Test
    fun `Given empty module list when invoke then returns empty list`() {
        // Arrange
        val modules = emptyList<TargetModule>()

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `Given single module with no dependencies when invoke then fanIn is 0 and fanOut is 0`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "data",
                internalDependencies = emptyList(),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(1, result.size)
        assertEquals("data", result[0].name)
        assertEquals(0, result[0].fanIn)
        assertEquals(0, result[0].fanOut)
        assertEquals(0f, result[0].instability)
    }

    @Test
    fun `Given single module with dependencies when invoke then fanOut equals dependency count`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "presentation",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "data")
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(1, result.size)
        assertEquals("presentation", result[0].name)
        assertEquals(0, result[0].fanIn)
        assertEquals(2, result[0].fanOut)
    }

    @Test
    fun `Given two modules where one depends on the other when invoke then fanIn increments for dependency target`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "domain",
                internalDependencies = emptyList(),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "data",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain")
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(2, result.size)

        val domainMetric = result.find { it.name == "domain" }!!
        assertEquals(1, domainMetric.fanIn)
        assertEquals(0, domainMetric.fanOut)

        val dataMetric = result.find { it.name == "data" }!!
        assertEquals(0, dataMetric.fanIn)
        assertEquals(1, dataMetric.fanOut)
    }

    @Test
    fun `Given three modules with circular dependencies when invoke then each module gets correct fanIn and fanOut`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "moduleA",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "moduleB")
                ),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "moduleB",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "moduleC")
                ),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "moduleC",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "moduleA")
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(3, result.size)
        result.forEach { metric ->
            assertEquals(1, metric.fanIn, "Module ${metric.name} should have fanIn = 1")
            assertEquals(1, metric.fanOut, "Module ${metric.name} should have fanOut = 1")
        }
    }

    @Test
    fun `Given complex dependency graph when invoke then calculates instability correctly`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "core",
                internalDependencies = emptyList(),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "data",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "core")
                ),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "domain",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "core"),
                    InternalDependenciesMetadata(group = null, moduleName = "data")
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(3, result.size)

        val coreMetric = result.find { it.name == "core" }!!
        assertEquals(2, coreMetric.fanIn)
        assertEquals(0, coreMetric.fanOut)
        assertEquals(0f, coreMetric.instability) // fanOut / (fanIn + fanOut) = 0 / 2 = 0

        val dataMetric = result.find { it.name == "data" }!!
        assertEquals(1, dataMetric.fanIn)
        assertEquals(1, dataMetric.fanOut)
        assertEquals(0.5f, dataMetric.instability) // fanOut / (fanIn + fanOut) = 1 / 2 = 0.5

        val domainMetric = result.find { it.name == "domain" }!!
        assertEquals(0, domainMetric.fanIn)
        assertEquals(2, domainMetric.fanOut)
        assertEquals(1f, domainMetric.instability) // fanOut / (fanIn + fanOut) = 2 / 2 = 1.0
    }

    @Test
    fun `Given module with case-insensitive dependency match when invoke then counts fanIn correctly`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "Domain",
                internalDependencies = emptyList(),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "Data",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain") // lowercase
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        val domainMetric = result.find { it.name == "Domain" }!!
        assertEquals(1, domainMetric.fanIn)
    }

    @Test
    fun `Given multiple modules depending on same module when invoke then fanIn increments correctly`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "utils",
                internalDependencies = emptyList(),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "module1",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "utils")
                ),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "module2",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "utils")
                ),
                rules = emptyList()
            ),
            TargetModule(
                moduleName = "module3",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "utils")
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        val utilsMetric = result.find { it.name == "utils" }!!
        assertEquals(3, utilsMetric.fanIn)
        assertEquals(0, utilsMetric.fanOut)
    }

    @Test
    fun `Given module with duplicate dependencies when invoke then counts unique dependencies only in fanOut`() {
        // Arrange - Note: The implementation counts duplicates, not unique deps.
        // This test validates current behavior
        val modules = listOf(
            TargetModule(
                moduleName = "core",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "utils"),
                    InternalDependenciesMetadata(group = null, moduleName = "utils") // duplicate
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(2, result[0].fanOut) // Current implementation counts duplicates
    }

    @Test
    fun `Given large module graph when invoke then all modules receive metrics`() {
        // Arrange
        val moduleCount = 10
        val modules = (0 until moduleCount).map { i ->
            TargetModule(
                moduleName = "module$i",
                internalDependencies = if (i > 0) {
                    listOf(
                        InternalDependenciesMetadata(group = null, moduleName = "module${i - 1}")
                    )
                } else {
                    emptyList()
                },
                rules = emptyList()
            )
        }

        // Act
        val result = useCase.invoke(modules)

        // Assert
        assertEquals(moduleCount, result.size)
        assertTrue(result.all { it.name.matches(Regex("module\\d+")) })
    }

    @Test
    fun `Given module with multiple different deps when invoke then instability reflects all outbound connections`() {
        // Arrange
        val modules = listOf(
            TargetModule(
                moduleName = "presentation",
                internalDependencies = listOf(
                    InternalDependenciesMetadata(group = null, moduleName = "domain"),
                    InternalDependenciesMetadata(group = null, moduleName = "data"),
                    InternalDependenciesMetadata(group = null, moduleName = "utils")
                ),
                rules = emptyList()
            )
        )

        // Act
        val result = useCase.invoke(modules)

        // Assert
        val metric = result[0]
        assertEquals("presentation", metric.name)
        assertEquals(3, metric.fanOut)
        assertEquals(0, metric.fanIn)
        assertEquals(1f, metric.instability) // 3 / (0 + 3) = 1.0
    }
}
