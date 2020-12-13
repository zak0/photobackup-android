package com.jamitek.photosapp.api

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * "Wrapper" for [Json] serializer to ensure same options across all usages.
 */
class ApiSerializer {

    companion object {
        private const val TAG = "ApiSerializer"
    }

    val jsonSerializer = Json { ignoreUnknownKeys = true }

    inline fun <reified T> deserialize(input: String): T {
        return jsonSerializer.decodeFromString(input)
    }

    inline fun <reified T> serialize(input: T): String {
        return Json.encodeToString(input)
    }
}
