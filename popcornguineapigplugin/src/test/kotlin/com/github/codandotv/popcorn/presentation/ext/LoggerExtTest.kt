package com.github.codandotv.popcorn.presentation.ext

import org.gradle.api.logging.Logger
import org.junit.Test
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggerExtTest {

    @Test
    fun `Given a logger when the deprecation is reported then it names the property and the version`() {
        val warnings = mutableListOf<String>()
        val logger = proxyLogger { method, args ->
            if (method == "warn") warnings.add(args.single() as String)
        }

        logger.popcornLoggerGroupNameDeprecated()

        val warning = warnings.single()
        assertTrue(warning.contains("groupName"), "warning must name the deprecated property")
        assertTrue(warning.contains("deprecated"), "warning must say the property is deprecated")
        assertTrue(warning.contains("4.0.0"), "warning must say when it is removed")
    }

    @Test
    fun `Given a logger when the group name deprecation is reported then it only warns`() {
        // A deprecation must stay a warning: it cannot fail a consumer build or hide in debug output.
        val calledMethods = mutableListOf<String>()
        val logger = proxyLogger { method, _ -> calledMethods.add(method) }

        logger.popcornLoggerGroupNameDeprecated()

        assertEquals(listOf("warn"), calledMethods)
    }

    private fun proxyLogger(onCall: (method: String, args: List<Any?>) -> Unit): Logger =
        Proxy.newProxyInstance(
            Logger::class.java.classLoader,
            arrayOf(Logger::class.java),
        ) { _, method, args ->
            onCall(method.name, args?.toList().orEmpty())
            null
        } as Logger
}
