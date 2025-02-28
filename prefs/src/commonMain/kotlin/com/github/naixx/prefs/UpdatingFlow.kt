package com.github.naixx.prefs

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

//this is not really observable settings flow
class UpdatingFlow<T>(
    private val settings: Settings,
    private val key: String,
    private val setter: Settings.(String, T) -> Unit,
    default: T,
    private val mut: MutableStateFlow<T> = MutableStateFlow(default)
) : StateFlow<T> by mut {

    fun update(value: T) {
        settings.setter(key, value)
        mut.value = value
    }

    operator fun plusAssign(value: T) {
        update(value)
    }
}

fun PrefsObject.booleanFlow(defaultValue: Boolean): ReadOnlyProperty<Any?, UpdatingFlow<Boolean>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Boolean>> {
        // Use a map with the instance as the key
        private val cachedFlows = mutableMapOf<Any, UpdatingFlow<Boolean>>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Boolean> {
            if (thisRef == null) return UpdatingFlow(settings, property.name, Settings::putBoolean, defaultValue)

            return cachedFlows.getOrPut(thisRef) {
                UpdatingFlow(
                    settings,
                    property.name,
                    Settings::putBoolean,
                    settings.getBooleanOrNull(property.name) ?: defaultValue
                )
            }
        }
    }

fun PrefsObject.intFlow(defaultValue: Int): ReadOnlyProperty<Any?, UpdatingFlow<Int>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Int>> {
        // Use a map with the instance as the key
        private val cachedFlows = mutableMapOf<Any, UpdatingFlow<Int>>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Int> {
            if (thisRef == null) return UpdatingFlow(settings, property.name, Settings::putInt, defaultValue)

            return cachedFlows.getOrPut(thisRef) {
                UpdatingFlow(
                    settings,
                    property.name,
                    Settings::putInt,
                    settings.getIntOrNull(property.name) ?: defaultValue
                )
            }
        }
    }

fun PrefsObject.longFlow(defaultValue: Long): ReadOnlyProperty<Any?, UpdatingFlow<Long>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Long>> {
        // Use a map with the instance as the key
        private val cachedFlows = mutableMapOf<Any, UpdatingFlow<Long>>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Long> {
            if (thisRef == null) return UpdatingFlow(settings, property.name, Settings::putLong, defaultValue)

            return cachedFlows.getOrPut(thisRef) {
                UpdatingFlow(
                    settings,
                    property.name,
                    Settings::putLong,
                    settings.getLongOrNull(property.name) ?: defaultValue
                )
            }
        }
    }

fun PrefsObject.floatFlow(defaultValue: Float): ReadOnlyProperty<Any?, UpdatingFlow<Float>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Float>> {
        // Use a map with the instance as the key
        private val cachedFlows = mutableMapOf<Any, UpdatingFlow<Float>>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Float> {
            if (thisRef == null) return UpdatingFlow(settings, property.name, Settings::putFloat, defaultValue)

            return cachedFlows.getOrPut(thisRef) {
                UpdatingFlow(
                    settings,
                    property.name,
                    Settings::putFloat,
                    settings.getFloatOrNull(property.name) ?: defaultValue
                )
            }
        }
    }

fun PrefsObject.doubleFlow(defaultValue: Double): ReadOnlyProperty<Any?, UpdatingFlow<Double>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Double>> {
        // Use a map with the instance as the key
        private val cachedFlows = mutableMapOf<Any, UpdatingFlow<Double>>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Double> {
            if (thisRef == null) return UpdatingFlow(settings, property.name, Settings::putDouble, defaultValue)

            return cachedFlows.getOrPut(thisRef) {
                UpdatingFlow(
                    settings,
                    property.name,
                    Settings::putDouble,
                    settings.getDoubleOrNull(property.name) ?: defaultValue
                )
            }
        }
    }

fun PrefsObject.stringFlow(defaultValue: String): ReadOnlyProperty<Any?, UpdatingFlow<String>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<String>> {
        // Use a map with the instance as the key
        private val cachedFlows = mutableMapOf<Any, UpdatingFlow<String>>()

        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<String> {
            if (thisRef == null) return UpdatingFlow(settings, property.name, Settings::putString, defaultValue)

            return cachedFlows.getOrPut(thisRef) {
                UpdatingFlow(
                    settings,
                    property.name,
                    Settings::putString,
                    settings.getStringOrNull(property.name) ?: defaultValue
                )
            }
        }
    }
