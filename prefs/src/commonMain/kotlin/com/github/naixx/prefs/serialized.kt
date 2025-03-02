package com.github.naixx.prefs

import com.russhwolf.settings.*
import com.russhwolf.settings.serialization.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <reified T> PrefsObject.serialized(
    defaultValue: T,
): ReadWriteProperty<Any?, T> =
    SerializationDelegate(this.settings, serializer<T>(), defaultValue)

inline fun <reified T : Any> PrefsObject.serializedNullable(): ReadWriteProperty<Any?, T?> =
    SerializationDelegate(this.settings, serializer<T>().nullable, null)

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
@PublishedApi
internal class SerializationDelegate<T>(
    private val settings: Settings,
    private val serializer: KSerializer<T>,
    private val defaultValue: T,
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return try {
            settings.decodeValue(serializer, property.name, defaultValue)
        } catch (e: Exception) {
            println(e) //i'm ok with that
            defaultValue
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        settings.encodeValue(serializer, property.name, value)
    }
}
