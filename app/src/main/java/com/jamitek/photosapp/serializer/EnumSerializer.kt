package com.jamitek.photosapp.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class EnumAsStringSerializer<T>(private val descriptorName: String) : KSerializer<T> {

    /**
     * All possible values of enum [T].
     *
     * TODO Maybe this could be done somehow with reflection without needing to manually expose the
     *  values.
     */
    abstract fun values(): Array<T>

    /**
     * Gets serialized value name for each of the enum values. This should has to be unique for each
     * of the values.
     */
    abstract fun serialName(forValue: T): String

    /**
     * Enum value to deserialize into when no match with [serialName] is found. Override this to
     * use a custom specific value. By default throws an exception.
     */
    open fun fallback(input: String): T = error("Couldn't deserialize '$input'")

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(descriptorName, PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T {
        val decoded = decoder.decodeString()
        return values().firstOrNull { serialName(it) == decoded } ?: fallback(decoded)
    }

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(serialName(value))
    }

}
