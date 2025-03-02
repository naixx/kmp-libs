package com.github.naixx.prefs

import com.russhwolf.settings.*
import com.russhwolf.settings.observable.makeObservable
import kotlinx.serialization.Serializable
import kotlin.test.*

@OptIn(ExperimentalSettingsApi::class)
class SerializedExtensionsTest {

    // Test serializable data classes
    @Serializable
    data class TestData(
        val stringValue: String,
        val intValue: Int,
        val booleanValue: Boolean
    )

    @Serializable
    data class ComplexData(
        val name: String,
        val numbers: List<Int>,
        val nested: TestData? = null
    )

    private class TestSerializedPrefs(override val settings: ObservableSettings): PrefsObject {

        var simpleData by serialized(TestData("default", 0, false))
        var complexData by serialized(ComplexData("default", emptyList()))
        var nullableData by serializedNullable<TestData>()
    }

    private lateinit var settings: ObservableSettings
    private lateinit var prefs: TestSerializedPrefs

    @BeforeTest
    fun setup() {
        settings = MapSettings().makeObservable()
        prefs = TestSerializedPrefs(settings)
    }

    @Test
    fun testDefaultValues() {
        // Test that default values are used when no value exists in settings
        assertEquals(TestData("default", 0, false), prefs.simpleData)
        assertEquals(ComplexData("default", emptyList()), prefs.complexData)
        assertNull(prefs.nullableData)
    }

    @Test
    fun testSimpleDataSerialization() {
        // Set a new value
        val newData = TestData("test", 42, true)
        prefs.simpleData = newData
        // Check that the value was stored correctly
        assertEquals(newData, prefs.simpleData)
        // Check persistence by creating a new prefs instance with the same settings
        val newPrefs = TestSerializedPrefs(settings)
        assertEquals(newData, newPrefs.simpleData)
    }

    @Test
    fun testComplexDataSerialization() {
        // Set a new value with nested data
        val nestedData = TestData("nested", 100, false)
        val newData = ComplexData("complex", listOf(1, 2, 3), nestedData)
        prefs.complexData = newData
        // Check that the value was stored correctly
        assertEquals(newData, prefs.complexData)
        assertEquals("complex", prefs.complexData.name)
        assertEquals(listOf(1, 2, 3), prefs.complexData.numbers)
        assertEquals(nestedData, prefs.complexData.nested)
        // Check persistence by creating a new prefs instance with the same settings
        val newPrefs = TestSerializedPrefs(settings)
        assertEquals(newData, newPrefs.complexData)
    }

    @Test
    fun testNullableDataSerialization() {
        // Initially null
        assertNull(prefs.nullableData)
        // Set a value
        val newData = TestData("nullable", 999, true)
        prefs.nullableData = newData
        assertEquals(newData, prefs.nullableData)
        // Set back to null
        prefs.nullableData = null
        assertNull(prefs.nullableData)
    }

    @Test
    fun testExceptionHandling() {
        // Create a new settings instance to reset state
        // This simulates what happens when data is missing or corrupted
        settings = MapSettings()
        prefs = TestSerializedPrefs(settings)
        // Reading should return the default value when no data exists
        assertEquals(TestData("default", 0, false), prefs.simpleData)
    }
}
