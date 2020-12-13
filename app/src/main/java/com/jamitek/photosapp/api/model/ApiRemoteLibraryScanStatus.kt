package com.jamitek.photosapp.api.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiRemoteLibraryScanStatus(
    val state: ApiLibraryState,
    val mediaFilesDetected: Int,
    val filesMoved: Int,
    val filesRemoved: Int,
    val newFiles: Int,
    val filesToProcess: Int,
    val filesProcessed: Int
)