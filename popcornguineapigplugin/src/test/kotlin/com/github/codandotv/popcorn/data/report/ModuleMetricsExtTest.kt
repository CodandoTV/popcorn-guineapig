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
            ModuleMetric(
                name = "data",
                fanIn = 0,
                fanOut = 1,
                instability = 1f,
                modulePath = ":data",
            )
        )

        // Act
        val result = metrics.toMetricsReport()

        // Assert
        assertEquals("name,fanIn,fanOut,instability,path\ndata,0,1,1.0,:data", result)
    }

    @Test
    fun `Given multiple metrics when toMetricsReport is called then returns header and all metric rows`() {
        // Arrange
        val metrics = listOf(
            ModuleMetric(
                name = "core",
                fanIn = 2,
                fanOut = 0,
                instability = 0f,
                modulePath = ":core",
            ),
            ModuleMetric(
                name = "data",
                fanIn = 1,
                fanOut = 1,
                instability = 0.5f,
                modulePath = ":data",
            ),
            ModuleMetric(
                name = "presentation",
                fanIn = 0,
                fanOut = 2,
                instability = 1f,
                modulePath = ":presentation",
            )
        )

        // Act
        val result = metrics.toMetricsReport()

        // Assert
        assertEquals(
            "name,fanIn,fanOut,instability,path\n" +
                    "core,2,0,0.0,:core\n" +
                    "data,1,1,0.5,:data\n" +
                    "presentation,0,2,1.0,:presentation",
            result
        )
    }

    @Test
    fun `Given two modules sharing a name when toMetricsReport is called then rows are distinguished by path`() {
        // Arrange
        val metrics = listOf(
            ModuleMetric(
                name = "data",
                fanIn = 1,
                fanOut = 0,
                instability = 0f,
                modulePath = ":car:data",
            ),
            ModuleMetric(
                name = "data",
                fanIn = 0,
                fanOut = 1,
                instability = 1f,
                modulePath = ":rent:data",
            )
        )

        // Act
        val result = metrics.toMetricsReport()

        // Assert
        assertEquals(
            "name,fanIn,fanOut,instability,path\n" +
                    "data,1,0,0.0,:car:data\n" +
                    "data,0,1,1.0,:rent:data",
            result
        )
    }
}
