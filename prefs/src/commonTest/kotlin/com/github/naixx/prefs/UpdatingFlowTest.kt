package com.github.naixx.prefs

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.observable.makeObservable
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Serializable
private enum class TestEnum {

    FIRST, SECOND, THIRD
}

@Serializable
private data class TestData(val x: Int, val y: String)

/**
 * Tests for UpdatingFlow focusing on property delegates
 */
class UpdatingFlowTest {

    // Test preference object
    private class TestPrefs(override var settings: ObservableSettings) : PrefsObject {
        val booleanPref by booleanFlow(false)
        val intPref by intFlow(0)
        val longPref by longFlow(0L)
        val floatPref by floatFlow(0.0f)
        val doublePref by doubleFlow(0.0)
        val stringPref by stringFlow("")

        // Preferences with non-default initial values
        val customBooleanPref by booleanFlow(true)
        val customIntPref by intFlow(42)
        val customLongPref by longFlow(9876543210L)
        val customFloatPref by floatFlow(3.14f)
        val customDoublePref by doubleFlow(2.7182818284)
        val customStringPref by stringFlow("Initial Value")

        // Enum/dataClass serializedFlow
        val enumPref by serializedFlow(TestEnum.FIRST)
        val customEnumPref by serializedFlow(TestEnum.SECOND)
        val dataPref by serializedFlow(TestData(0, ""))
        val customDataPref by serializedFlow(TestData(42, "KMP"))
    }

    // Shared test objects
    private lateinit var settings: ObservableSettings
    private lateinit var testPrefs: TestPrefs

    @BeforeTest
    fun setup() {
        settings = MapSettings().makeObservable()
        testPrefs = TestPrefs(settings)
    }

    @Test
    fun testInitialDefaultValues() {
        // Test that default values are used when no value exists in settings
        assertEquals(false, testPrefs.booleanPref.value)
        assertEquals(0, testPrefs.intPref.value)
        assertEquals(0L, testPrefs.longPref.value)
        assertEquals(0.0f, testPrefs.floatPref.value)
        assertEquals(0.0, testPrefs.doublePref.value)
        assertEquals("", testPrefs.stringPref.value)
    }

    @Test
    fun testCustomInitialValues() {
        // Test initial values specified in the delegate calls
        assertEquals(true, testPrefs.customBooleanPref.value)
        assertEquals(42, testPrefs.customIntPref.value)
        assertEquals(9876543210L, testPrefs.customLongPref.value)
        assertEquals(3.14f, testPrefs.customFloatPref.value)
        assertEquals(2.7182818284, testPrefs.customDoublePref.value)
        assertEquals("Initial Value", testPrefs.customStringPref.value)
    }

    @Test
    fun testPredefinedSettingsValues() {
        // Create settings with predefined values
        settings.apply {
            putBoolean("booleanPref", true)
            putInt("intPref", 123)
            putLong("longPref", 456L)
            putFloat("floatPref", 7.89f)
            putDouble("doublePref", 1.23456)
            putString("stringPref", "Predefined Value")
        }

        // Create a new TestPrefs instance with the pre-populated settings
        val prefsWithValues = TestPrefs(settings)

        // Test that values from settings are used instead of defaults
        assertEquals(true, prefsWithValues.booleanPref.value)
        assertEquals(123, prefsWithValues.intPref.value)
        assertEquals(456L, prefsWithValues.longPref.value)
        assertEquals(7.89f, prefsWithValues.floatPref.value)
        assertEquals(1.23456, prefsWithValues.doublePref.value)
        assertEquals("Predefined Value", prefsWithValues.stringPref.value)
    }

    @Test
    fun testMultiplePrefsInstances() {
        // Create two instances with the same settings
        val testPrefs1 = TestPrefs(settings)
        val testPrefs2 = TestPrefs(settings)

        // Update a value in the first instance
        testPrefs1.booleanPref.update(true)

        // The change should be reflected in both instances through the shared settings
        assertEquals(true, testPrefs1.booleanPref.value)
        assertEquals(true, testPrefs2.booleanPref.value)

        // Test that changes from the second instance also affect the first
        testPrefs2.intPref.update(42)
        assertEquals(42, testPrefs1.intPref.value)
        assertEquals(42, testPrefs2.intPref.value)

        // Test with multiple property types to ensure full synchronization
        testPrefs1.stringPref.update("Shared Data")
        testPrefs2.longPref.update(999L)

        assertEquals("Shared Data", testPrefs2.stringPref.value)
        assertEquals(999L, testPrefs1.longPref.value)
    }

    @Test
    fun testBooleanFlow() {
        // Test default value
        assertEquals(false, testPrefs.booleanPref.value)

        // Test update method
        testPrefs.booleanPref.update(true)
        assertEquals(true, testPrefs.booleanPref.value)
        assertEquals(true, settings.getBoolean("booleanPref", false))

        // Test plusAssign operator
        testPrefs.booleanPref += false
        assertEquals(false, testPrefs.booleanPref.value)
        assertEquals(false, settings.getBoolean("booleanPref", true))
    }

    @Test
    fun testIntFlow() {
        // Test default value
        assertEquals(0, testPrefs.intPref.value)

        // Test update method
        testPrefs.intPref.update(42)
        assertEquals(42, testPrefs.intPref.value)
        assertEquals(42, settings.getInt("intPref", 0))

        // Test plusAssign operator
        testPrefs.intPref += 100
        assertEquals(100, testPrefs.intPref.value)
        assertEquals(100, settings.getInt("intPref", 0))
    }

    @Test
    fun testLongFlow() {
        // Test default value
        assertEquals(0L, testPrefs.longPref.value)

        // Test update method
        testPrefs.longPref.update(1234567890L)
        assertEquals(1234567890L, testPrefs.longPref.value)
        assertEquals(1234567890L, settings.getLong("longPref", 0L))

        // Test plusAssign operator
        testPrefs.longPref += 9876543210L
        assertEquals(9876543210L, testPrefs.longPref.value)
        assertEquals(9876543210L, settings.getLong("longPref", 0L))
    }

    @Test
    fun testFloatFlow() {
        // Test default value
        assertEquals(0.0f, testPrefs.floatPref.value)

        // Test update method
        testPrefs.floatPref.update(3.14f)
        assertEquals(3.14f, testPrefs.floatPref.value)
        assertEquals(3.14f, settings.getFloat("floatPref", 0.0f))

        // Test plusAssign operator
        testPrefs.floatPref += 2.71f
        assertEquals(2.71f, testPrefs.floatPref.value)
        assertEquals(2.71f, settings.getFloat("floatPref", 0.0f))
    }

    @Test
    fun testDoubleFlow() {
        // Test default value
        assertEquals(0.0, testPrefs.doublePref.value)

        // Test update method
        testPrefs.doublePref.update(3.14159265359)
        assertEquals(3.14159265359, testPrefs.doublePref.value)
        assertEquals(3.14159265359, settings.getDouble("doublePref", 0.0))

        // Test plusAssign operator
        testPrefs.doublePref += 2.7182818284
        assertEquals(2.7182818284, testPrefs.doublePref.value)
        assertEquals(2.7182818284, settings.getDouble("doublePref", 0.0))
    }

    @Test
    fun testStringFlow() {
        // Test default value
        assertEquals("", testPrefs.stringPref.value)

        // Test update method
        testPrefs.stringPref.update("Hello, World!")
        assertEquals("Hello, World!", testPrefs.stringPref.value)
        assertEquals("Hello, World!", settings.getString("stringPref", ""))

        // Test plusAssign operator
        testPrefs.stringPref += "Kotlin Multiplatform"
        assertEquals("Kotlin Multiplatform", testPrefs.stringPref.value)
        assertEquals("Kotlin Multiplatform", settings.getString("stringPref", ""))
    }

    @Test
    fun testSerializedEnumDefaultAndCustom() {
        // Default value
        assertEquals(TestEnum.FIRST, testPrefs.enumPref.value)
        // Custom non-default
        assertEquals(TestEnum.SECOND, testPrefs.customEnumPref.value)
    }

    @Test
    fun testSerializedEnumUpdateAndAssign() {
        testPrefs.enumPref.update(TestEnum.SECOND)
        assertEquals(TestEnum.SECOND, testPrefs.enumPref.value)
        testPrefs.enumPref += TestEnum.THIRD
        assertEquals(TestEnum.THIRD, testPrefs.enumPref.value)
        val newPrefs = TestPrefs(settings)
        assertEquals(TestEnum.THIRD, newPrefs.enumPref.value)
    }

    @Test
    fun testSerializedDataDefaultAndCustom() {
        // Default value
        assertEquals(TestData(0, ""), testPrefs.dataPref.value)
        // Custom non-default
        assertEquals(TestData(42, "KMP"), testPrefs.customDataPref.value)
    }

    @Test
    fun testSerializedDataUpdateAndAssign() {
        testPrefs.dataPref.update(TestData(7, "abc"))
        assertEquals(TestData(7, "abc"), testPrefs.dataPref.value)
        testPrefs.dataPref += TestData(123, "xyz")
        assertEquals(TestData(123, "xyz"), testPrefs.dataPref.value)
        val newPrefs = TestPrefs(settings)
        assertEquals(TestData(123, "xyz"), newPrefs.dataPref.value)
    }

    @Test
    fun testSerializedPredefinedSettingsValues() {
        // Write, then re-read with new TestPrefs instance
        testPrefs.enumPref.update(TestEnum.SECOND)
        testPrefs.dataPref.update(TestData(100, "Hello"))
        val newPrefs = TestPrefs(settings)
        assertEquals(TestEnum.SECOND, newPrefs.enumPref.value)
        assertEquals(TestData(100, "Hello"), newPrefs.dataPref.value)
    }
}
