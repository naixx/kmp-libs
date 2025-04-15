package com.github.naixx.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import kotlinx.coroutines.flow.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.serializer
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
        private var cachedFlow: UpdatingFlow<Boolean>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Boolean> {
            if (cachedFlow == null) {
                cachedFlow = UpdatingFlow(
                    this@booleanFlow.settings,
                    property.name,
                    Settings::putBoolean,
                    this@booleanFlow.settings.getBooleanOrNull(property.name) ?: defaultValue
                )
            }
            return cachedFlow!!
        }
    }

fun PrefsObject.intFlow(defaultValue: Int): ReadOnlyProperty<Any?, UpdatingFlow<Int>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Int>> {
        private var cachedFlow: UpdatingFlow<Int>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Int> {
            if (cachedFlow == null) {
                cachedFlow = UpdatingFlow(
                    this@intFlow.settings,
                    property.name,
                    Settings::putInt,
                    this@intFlow.settings.getIntOrNull(property.name) ?: defaultValue
                )
            }
            return cachedFlow!!
        }
    }

fun PrefsObject.longFlow(defaultValue: Long): ReadOnlyProperty<Any?, UpdatingFlow<Long>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Long>> {
        private var cachedFlow: UpdatingFlow<Long>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Long> {
            if (cachedFlow == null) {
                cachedFlow = UpdatingFlow(
                    this@longFlow.settings,
                    property.name,
                    Settings::putLong,
                    this@longFlow.settings.getLongOrNull(property.name) ?: defaultValue
                )
            }
            return cachedFlow!!
        }
    }

fun PrefsObject.floatFlow(defaultValue: Float): ReadOnlyProperty<Any?, UpdatingFlow<Float>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Float>> {
        private var cachedFlow: UpdatingFlow<Float>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Float> {
            if (cachedFlow == null) {
                cachedFlow = UpdatingFlow(
                    this@floatFlow.settings,
                    property.name,
                    Settings::putFloat,
                    this@floatFlow.settings.getFloatOrNull(property.name) ?: defaultValue
                )
            }
            return cachedFlow!!
        }
    }

fun PrefsObject.doubleFlow(defaultValue: Double): ReadOnlyProperty<Any?, UpdatingFlow<Double>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<Double>> {
        private var cachedFlow: UpdatingFlow<Double>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<Double> {
            if (cachedFlow == null) {
                cachedFlow = UpdatingFlow(
                    this@doubleFlow.settings,
                    property.name,
                    Settings::putDouble,
                    this@doubleFlow.settings.getDoubleOrNull(property.name) ?: defaultValue
                )
            }
            return cachedFlow!!
        }
    }

fun PrefsObject.stringFlow(defaultValue: String): ReadOnlyProperty<Any?, UpdatingFlow<String>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<String>> {
        private var cachedFlow: UpdatingFlow<String>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<String> {
            if (cachedFlow == null) {
                cachedFlow = UpdatingFlow(
                    this@stringFlow.settings,
                    property.name,
                    Settings::putString,
                    this@stringFlow.settings.getStringOrNull(property.name) ?: defaultValue
                )
            }
            return cachedFlow!!
        }
    }


@OptIn(ExperimentalSettingsApi::class, ExperimentalSerializationApi::class)
inline fun <reified T> PrefsObject.serializedFlow(defaultValue: T): ReadOnlyProperty<Any?, UpdatingFlow<T>> =
    object : ReadOnlyProperty<Any?, UpdatingFlow<T>> {
        private var cachedFlow: UpdatingFlow<T>? = null
        override fun getValue(thisRef: Any?, property: KProperty<*>): UpdatingFlow<T> {
            if (cachedFlow == null) {
                val serializer = serializer<T>()
                cachedFlow = UpdatingFlow(
                    this@serializedFlow.settings,
                    property.name,
                    { key, value ->
                        settings.encodeValue(serializer, key, value)
                    },
                    settings.decodeValue(serializer, property.name, defaultValue)
                )
            }
            return cachedFlow!!
        }
    }
