package com.github.codandotv.popcorn.data.report


import com.github.codandotv.popcorn.domain.models.ModuleMetric
import org.junit.Test
import kotlin.test.assertEquals

class ModuleMetricsExtTest {

    @Test
    fun `Given empty metrics list when toMetricsReport is called then returns only header`() {
        // Arrange
        val metrics = emptyList<ModuleMetric>()

        // Act
        val result = metrics.toMetricsReport()

        // Assert
        assertEquals("", result)
    }

    @Test
    fun `Given single metric when toMetricsReport is called then returns header and metric row`() {
        // Arrange
        val metrics = listOf(
            ModuleMetric(name = "data", fanIn = 0, fanOut = 1, instability = 1f)
        )

        // Act
        val result = metrics.toMetricsReport()

        // Assert
        assertEquals("name,fanIn,fanOut,instability\ndata,0,1,1.0", result)
    }

    @Test
    fun `Given multiple metrics when toMetricsReport is called then returns header and all metric rows`() {
        // Arrange
        val metrics = listOf(
            ModuleMetric(name = "core", fanIn = 2, fanOut = 0, instability = 0f),
            ModuleMetric(name = "data", fanIn = 1, fanOut = 1, instability = 0.5f),
            ModuleMetric(name = "presentation", fanIn = 0, fanOut = 2, instability = 1f)
        )

        // Act
        val result = metrics.toMetricsReport()

        // Assert
        assertEquals(
            "name,fanIn,fanOut,instability\ncore,2,0,0.0\ndata,1,1,0.5\npresentation,0,2,1.0",
            result
        )
    }
}
