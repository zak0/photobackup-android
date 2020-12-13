package com.jamitek.photosapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiMedia(
    val id: Int,
    val fileName: String,
    val fileSize: Long,
    val dirPath: String,
    val checksum: String,
    val dateTimeOriginal: String,
    val status: ApiMediaStatus
)