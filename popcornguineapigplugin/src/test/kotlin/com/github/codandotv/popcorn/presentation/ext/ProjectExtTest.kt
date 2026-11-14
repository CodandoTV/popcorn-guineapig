package com.github.codandotv.popcorn.presentation.ext

import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.junit.Test
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProjectExtTest {

    @Test
    fun `Given a project dependency in the project group when mapped then keeps its path`() {
        val dependency = dependencyProxy(
            type = ProjectDependency::class.java,
            group = "com.popcorn",
            name = "data",
            path = ":car:data",
        )

        val result = dependency.toInternalDependencyOrNull("com.popcorn")

        assertEquals(":car:data", result?.modulePath)
        assertEquals("data", result?.moduleName)
        assertEquals("com.popcorn", result?.group)
    }

    @Test
    fun `Given an external dependency in the project group when mapped then path is null`() {
        val dependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "com.popcorn",
            name = "data",
            path = null,
        )

        val result = dependency.toInternalDependencyOrNull("com.popcorn")

        assertNull(result?.modulePath)
        assertEquals("data", result?.moduleName)
    }

    @Test
    fun `Given a dependency outside the project group when mapped then it is not internal`() {
        val dependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "org.jetbrains",
            name = "kotlin-stdlib",
            path = null,
        )

        assertNull(dependency.toInternalDependencyOrNull("com.popcorn"))
    }

    @Test
    fun `Given a dependency without group when mapped then it is not internal`() {
        val dependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = null,
            name = "data",
            path = null,
        )

        assertNull(dependency.toInternalDependencyOrNull("com.popcorn"))
    }

    private fun <T> dependencyProxy(
        type: Class<T>,
        group: String?,
        name: String,
        path: String?,
    ): T {
        return type.cast(
            Proxy.newProxyInstance(
                type.classLoader,
                arrayOf(type),
                InvocationHandler { _, method, _ ->
                    when (method.name) {
                        "getGroup" -> group
                        "getName" -> name
                        "getPath" -> path
                        else -> null
                    }
                },
            )
        )
    }
}
