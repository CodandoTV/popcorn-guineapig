package com.github.codandotv.popcorn.domain.usecases.metric

import com.github.codandotv.popcorn.domain.models.ModuleMetric
import com.github.codandotv.popcorn.domain.models.TargetModule
import com.github.codandotv.popcorn.fakes.FakeLogger
import com.github.codandotv.popcorn.fakes.fakePopcornGuineapigRepositoryWithCallbacks
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerateMetricsReportUseCaseTest {

    @Test
    fun `Given modules when invoke then collectModuleMetricsUseCase is called with correct modules`() {
        // Arrange
        val fakeLogger = FakeLogger(mutableListOf(), mutableListOf())
        var capturedModulesList: List<TargetModule>? = null

        var metricsReportCall = false

        val fakeRepository = fakePopcornGuineapigRepositoryWithCallbacks(
            onMetricsReportCallback = {
                metricsReportCall = true
            },
            onErrorReportCallback = {
                error("Illegal call")
            }
        )

        val fakeCollectMetricsUseCase = object : CollectModuleMetricsUseCase {
            override fun invoke(modules: List<TargetModule>): List<ModuleMetric> {
                capturedModulesList = modules
                return emptyList()
            }
        }

        val generateMetricsReportUseCase = GenerateMetricsReportUseCaseImpl(
            collectModuleMetricsUseCase = fakeCollectMetricsUseCase,
            repository = fakeRepository,
            logger = fakeLogger
        )

        val expectedModules = listOf(
            TargetModule("data", emptyList(), emptyList()),
            TargetModule("domain", emptyList(), emptyList())
        )

        // Act
        generateMetricsReportUseCase.invoke(
            metricsReportPath = "/tmp/report.csv",
            modules = expectedModules
        )

        // Assert
        assertNotNull(capturedModulesList)
        assertEquals(expectedModules, capturedModulesList)
        assertTrue(metricsReportCall)
    }
}
