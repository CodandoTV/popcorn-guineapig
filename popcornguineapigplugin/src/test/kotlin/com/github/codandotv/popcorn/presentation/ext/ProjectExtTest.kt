package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
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

    @Test
    fun `Given the same project declared in two source sets when deduplicated then keeps one entry`() {
        // A KMP module may declare project(":car:data") in both commonMain and androidMain.
        val declaredInCommonMain = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":car:data",
        )
        val declaredInAndroidMain = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":car:data",
        )

        val result = listOf(declaredInCommonMain, declaredInAndroidMain).distinctDependencies()

        assertEquals(listOf(declaredInCommonMain), result)
    }

    @Test
    fun `Given coordinate-only dependencies differing only in case when deduplicated then keeps one entry`() {
        val declaredInCommonMain = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "Data",
            modulePath = null,
        )
        val declaredInAndroidMain = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = null,
        )

        val result = listOf(declaredInCommonMain, declaredInAndroidMain).distinctDependencies()

        assertEquals(1, result.size)
        assertEquals("Data", result.single().moduleName)
    }

    @Test
    fun `Given modules sharing a leaf name when deduplicated then keeps both entries`() {
        val carData = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":car:data",
        )
        val rentData = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":rent:data",
        )

        val result = listOf(carData, rentData).distinctDependencies()

        assertEquals(listOf(carData, rentData), result)
    }

    @Test
    fun `Given repeated entries interleaved with others when deduplicated then keeps the first occurrence order`() {
        val domain = InternalDependenciesMetadata(group = "com.popcorn", moduleName = "domain", modulePath = ":domain")
        val data = InternalDependenciesMetadata(group = "com.popcorn", moduleName = "data", modulePath = ":data")

        val result = listOf(domain, data, domain).distinctDependencies()

        assertEquals(listOf(domain, data), result)
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
