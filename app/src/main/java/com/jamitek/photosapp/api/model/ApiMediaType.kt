package com.jamitek.photosapp.api.model

import kotlinx.serialization.Serializable

@Serializable
enum class ApiMediaType {
    Picture,
    Video;

    companion object {
        fun fromString(value: String) = values().first { it.name == value }
    }
}
