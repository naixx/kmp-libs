package com.github.naixx.prefs

import com.russhwolf.settings.*
import com.russhwolf.settings.serialization.*
import kotlinx.serialization.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> PrefsObject.serialized(
    defaultValue: T,
): ReadWriteProperty<Any?, T> =
    SerializationDelegate(this.settings, serializer<T>(), defaultValue)

inline fun <reified T : Any?> PrefsObject.serialized(): ReadWriteProperty<Any?, T?> =
    NullableSerializationDelegate(this.settings, serializer<T?>())

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@PublishedApi
internal class SerializationDelegate<T>(
    private val settings: Settings,
    private val serializer: KSerializer<T>,
    private val defaultValue: T,
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return settings.decodeValue(serializer, property.name, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        settings.encodeValue(serializer, property.name, value)
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@PublishedApi
internal class NullableSerializationDelegate<T>(
    private val settings: Settings,
    private val serializer: KSerializer<T?>,
) : ReadWriteProperty<Any?, T?> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        return settings.decodeValue(serializer, property.name, null)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        settings.encodeValue(serializer, property.name, value)
    }
}
