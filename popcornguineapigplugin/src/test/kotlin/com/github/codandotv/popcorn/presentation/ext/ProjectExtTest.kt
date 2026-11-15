package com.github.codandotv.popcorn.presentation.ext

import com.github.codandotv.popcorn.domain.models.InternalDependenciesMetadata
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.logging.Logger
import org.junit.Test
import java.lang.reflect.Method
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
    fun `Given a project dependency outside the configured group when mapped then it is internal`() {
        // A subproject can set its own group; the type still proves the dependency is internal.
        val dependency = dependencyProxy(
            type = ProjectDependency::class.java,
            group = "com.mycompany",
            name = "core",
            path = ":core",
        )

        val result = dependency.toInternalDependencyOrNull("com.popcorn")

        assertEquals(":core", result?.modulePath)
        assertEquals("core", result?.moduleName)
    }

    @Test
    fun `Given a project dependency without group when mapped then it is internal`() {
        val dependency = dependencyProxy(
            type = ProjectDependency::class.java,
            group = null,
            name = "core",
            path = ":core",
        )

        assertEquals(":core", dependency.toInternalDependencyOrNull(null)?.modulePath)
    }

    @Test
    fun `Given an external dependency sharing only a group prefix when mapped then it is not internal`() {
        val dependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "com.popcornx",
            name = "data",
            path = null,
        )

        assertNull(dependency.toInternalDependencyOrNull("com.popcorn"))
    }

    @Test
    fun `Given an external dependency ending with the configured group when mapped then it is not internal`() {
        val dependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "com.example.core",
            name = "utils",
            path = null,
        )

        assertNull(dependency.toInternalDependencyOrNull("core"))
    }

    @Test
    fun `Given an external dependency inside the configured group when mapped then it is internal`() {
        val dependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "com.popcorn.data",
            name = "data",
            path = null,
        )

        val result = dependency.toInternalDependencyOrNull("com.popcorn")

        assertNull(result?.modulePath)
        assertEquals("data", result?.moduleName)
    }

    @Test
    fun `Given a root project named android when an androidx dependency is extracted then it is not internal`() {
        // The root name used to be the detection needle, so "androidx.core" looked internal.
        val androidxDependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "androidx.core",
            name = "core-ktx",
            path = null,
        )
        val project = projectProxy(
            name = "app",
            rootProjectName = "android",
            configurations = listOf(configurationProxy("implementation", listOf(androidxDependency))),
        )

        val result = project.internalProjectDependencies(
            configurationNames = listOf("implementation"),
            groupName = null,
        )

        assertEquals(emptyList<InternalDependenciesMetadata>(), result)
    }

    @Test
    fun `Given a project dependency in a custom group when extracted then it is internal with its path`() {
        val coreDependency = dependencyProxy(
            type = ProjectDependency::class.java,
            group = "com.mycompany",
            name = "core",
            path = ":core",
        )
        val project = projectProxy(
            name = "app",
            rootProjectName = "popcorn-app",
            configurations = listOf(configurationProxy("implementation", listOf(coreDependency))),
        )

        val result = project.internalProjectDependencies(
            configurationNames = listOf("implementation"),
            groupName = null,
        )

        assertEquals(1, result.size)
        assertEquals(":core", result.single().modulePath)
    }

    @Test
    fun `Given a blank group name when coordinates are extracted then they are not internal`() {
        val coordinateDependency = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "com.popcorn",
            name = "data",
            path = null,
        )
        val project = projectProxy(
            name = "app",
            configurations = listOf(configurationProxy("implementation", listOf(coordinateDependency))),
        )

        val result = project.internalProjectDependencies(
            configurationNames = listOf("implementation"),
            groupName = "   ",
        )

        assertEquals(emptyList<InternalDependenciesMetadata>(), result)
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

    @Test
    fun `Given a coordinate entry before its project declaration when deduplicated then keeps the project entry`() {
        val coordinateOnly = InternalDependenciesMetadata(group = "com.popcorn", moduleName = "data", modulePath = null)
        val declaredAsProject = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":car:data",
        )

        val result = listOf(coordinateOnly, declaredAsProject).distinctDependencies()

        assertEquals(listOf(declaredAsProject), result)
    }

    @Test
    fun `Given a project declaration followed by a coordinate entry when deduplicated then keeps the project entry`() {
        val declaredAsProject = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":car:data",
        )
        val coordinateOnly = InternalDependenciesMetadata(group = "com.popcorn", moduleName = "data", modulePath = null)

        val result = listOf(declaredAsProject, coordinateOnly).distinctDependencies()

        assertEquals(listOf(declaredAsProject), result)
    }

    @Test
    fun `Given a project path and an unrelated coordinate-only dependency when deduplicated then keeps both`() {
        val declaredAsProject = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "data",
            modulePath = ":car:data",
        )
        val unrelatedCoordinate = InternalDependenciesMetadata(
            group = "com.popcorn",
            moduleName = "resources",
            modulePath = null,
        )

        val result = listOf(declaredAsProject, unrelatedCoordinate).distinctDependencies()

        assertEquals(listOf(declaredAsProject, unrelatedCoordinate), result)
    }

    @Test
    fun `Given the same project dependency declared in two source sets when extracted then keeps one dependency`() {
        // Arrange - A KMP module can expose project(":car:data") from commonMainImplementation and
        // androidMainImplementation. Gradle yields one entry per declaration.
        val dataDependency = dependencyProxy(
            type = ProjectDependency::class.java,
            group = "com.popcorn",
            name = "data",
            path = ":car:data",
        )
        val project = projectProxy(
            name = "app",
            configurations = listOf(
                configurationProxy("commonMainImplementation", listOf(dataDependency, dataDependency)),
                configurationProxy("androidMainImplementation", listOf(dataDependency)),
            ),
        )

        // Act
        val result = project.internalProjectDependencies(
            configurationNames = listOf("commonMainImplementation", "androidMainImplementation"),
            groupName = "com.popcorn",
        )

        // Assert
        assertEquals(1, result.size)
        assertEquals(":car:data", result.single().modulePath)
    }

    @Test
    fun `Given a coordinate declaration before the project one when extracted then keeps the project dependency`() {
        // Arrange
        val dataCoordinate = dependencyProxy(
            type = ModuleDependency::class.java,
            group = "com.popcorn",
            name = "data",
            path = null,
        )
        val dataProjectDependency = dependencyProxy(
            type = ProjectDependency::class.java,
            group = "com.popcorn",
            name = "data",
            path = ":car:data",
        )
        val project = projectProxy(
            name = "app",
            configurations = listOf(
                configurationProxy("commonMainImplementation", listOf(dataCoordinate)),
                configurationProxy("androidMainImplementation", listOf(dataProjectDependency)),
            ),
        )

        // Act
        val result = project.internalProjectDependencies(
            configurationNames = listOf("commonMainImplementation", "androidMainImplementation"),
            groupName = "com.popcorn",
        )

        // Assert
        assertEquals(1, result.size)
        assertEquals(":car:data", result.single().modulePath)
    }

    private fun <T> dependencyProxy(
        type: Class<T>,
        group: String?,
        name: String,
        path: String?,
    ): T = proxy(type) { method ->
        when (method.name) {
            "getGroup" -> group
            "getName" -> name
            "getPath" -> path
            else -> null
        }
    }

    private fun projectProxy(
        name: String,
        configurations: List<Configuration>,
        rootProjectName: String = name,
    ): Project {
        val rootProject = proxy(Project::class.java) { method ->
            when (method.name) {
                "getName" -> rootProjectName
                "getPath" -> ":"
                else -> null
            }
        }
        val logger = proxy(Logger::class.java) { null }
        val configurationContainer = proxy(ConfigurationContainer::class.java) { method ->
            if (method.name == "iterator") configurations.iterator() else null
        }

        lateinit var project: Project
        project = proxy(Project::class.java) { method ->
            when (method.name) {
                "getName" -> name
                "getPath" -> ":$name"
                "getProject" -> project
                "getRootProject" -> rootProject
                "getConfigurations" -> configurationContainer
                "getLogger" -> logger
                else -> null
            }
        }
        return project
    }

    private fun configurationProxy(
        name: String,
        dependencies: List<Dependency>,
    ): Configuration = proxy(Configuration::class.java) { method ->
        when (method.name) {
            "getName" -> name
            "getDependencies" -> dependencySetProxy(dependencies)
            else -> null
        }
    }

    private fun dependencySetProxy(dependencies: List<Dependency>): DependencySet =
        proxy(DependencySet::class.java) { method ->
            if (method.name == "iterator") dependencies.iterator() else null
        }

    private fun <T> proxy(type: Class<T>, handler: (Method) -> Any?): T =
        type.cast(
            Proxy.newProxyInstance(type.classLoader, arrayOf(type)) { _, method, _ ->
                handler(method)
            }
        )
}
