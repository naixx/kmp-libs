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

    @Serializable
    data class DataWithNullableFields(
        val id: Int,
        val name: String?,
        val tags: List<String>?
    )

    private class TestSerializedPrefs(override val settings: ObservableSettings) : PrefsObject {

        var simpleData by serialized(TestData("default", 0, false))
        var complexData by serialized(ComplexData("default", emptyList()))
        var nullableData by serialized<TestData>()
        var set by serialized(setOf("a"))
        var dataWithNullableFields by serialized(DataWithNullableFields(0, null, null))
        var nullableComplexData by serialized<ComplexData>()
        var nullableList by serialized<List<String>>()
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
    fun testNullableFieldsInData() {
        // Test default state
        assertEquals(0, prefs.dataWithNullableFields.id)
        assertNull(prefs.dataWithNullableFields.name)
        assertNull(prefs.dataWithNullableFields.tags)

        // Update with non-null values
        val updatedData = DataWithNullableFields(1, "Test Name", listOf("tag1", "tag2"))
        prefs.dataWithNullableFields = updatedData
        assertEquals(updatedData, prefs.dataWithNullableFields)
        assertEquals("Test Name", prefs.dataWithNullableFields.name)
        assertEquals(listOf("tag1", "tag2"), prefs.dataWithNullableFields.tags)

        // Check persistence
        val newPrefs = TestSerializedPrefs(settings)
        assertEquals(updatedData, newPrefs.dataWithNullableFields)

        // Set back to partial null values
        val partialNullData = DataWithNullableFields(2, null, listOf("tag3"))
        prefs.dataWithNullableFields = partialNullData
        assertEquals(partialNullData, prefs.dataWithNullableFields)
        assertNull(prefs.dataWithNullableFields.name)
        assertNotNull(prefs.dataWithNullableFields.tags)
    }

    @Test
    fun testNullableComplexDataSerialization() {
        // Initially null
        assertNull(prefs.nullableComplexData)

        // Set with null nested field
        val complexWithNullNested = ComplexData("complex-null-nested", listOf(5, 6, 7))
        prefs.nullableComplexData = complexWithNullNested
        assertEquals(complexWithNullNested, prefs.nullableComplexData)
        assertNull(prefs.nullableComplexData?.nested)

        // Set with non-null nested field
        val nestedData = TestData("inner-data", 42, true)
        val complexWithNested = ComplexData("complex-with-nested", listOf(1, 2, 3), nestedData)
        prefs.nullableComplexData = complexWithNested
        assertEquals(complexWithNested, prefs.nullableComplexData)
        assertEquals(nestedData, prefs.nullableComplexData?.nested)

        // Set back to null
        prefs.nullableComplexData = null
        assertNull(prefs.nullableComplexData)
    }

    @Test
    fun testNullableCollectionSerialization() {
        // Initially null
        assertNull(prefs.nullableList)

        // Set empty list
        prefs.nullableList = emptyList()
        assertNotNull(prefs.nullableList)
        assertTrue(prefs.nullableList?.isEmpty() == true)

        // Set populated list
        prefs.nullableList = listOf("one", "two", "three")
        assertEquals(3, prefs.nullableList?.size)
        assertEquals("two", prefs.nullableList?.get(1))

        // Set back to null
        prefs.nullableList = null
        assertNull(prefs.nullableList)
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

    @Test
    fun testCollectionSerialization() {
        assertTrue(prefs.set.all { it == "a" })
        prefs.set += "b"
        assertEquals(setOf("a", "b"), prefs.set)
        prefs.set -= "a"
        assertEquals(setOf("b"), prefs.set)
        prefs.set += "c"
        assertEquals(setOf("b", "c"), prefs.set)
        prefs.set = setOf()
        assertTrue(prefs.set.isEmpty())
    }
}
