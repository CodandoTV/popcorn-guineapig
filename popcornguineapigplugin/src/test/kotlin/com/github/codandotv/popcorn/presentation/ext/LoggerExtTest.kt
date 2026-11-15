package com.github.codandotv.popcorn.presentation.ext

import org.gradle.api.logging.Logger
import org.junit.Test
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoggerExtTest {

    @Test
    fun `Given a logger when the implicit root group is reported then it names the property and the dependencies`() {
        val warnings = mutableListOf<String>()
        val calledMethods = mutableListOf<String>()
        val logger = proxyLogger { method, args ->
            calledMethods.add(method)
            if (method == "warn") warnings.add(args.single() as String)
        }

        logger.popcornLoggerImplicitGroupNameRemoved(
            rootProjectName = "popcorn",
            dependencyIds = listOf("com.popcorn:data", "com.popcorn:domain"),
        )

        val warning = warnings.single()
        assertTrue(warning.contains("groupName"), "warning must name the property to configure")
        assertTrue(warning.contains("popcorn"), "warning must name the removed implicit group")
        assertTrue(warning.contains("com.popcorn:data"), "warning must name the affected dependency")
        assertTrue(warning.contains("com.popcorn:domain"), "warning must list every affected dependency")
        assertEquals(listOf("warn"), calledMethods, "the migration signal must not fail a consumer build")
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
