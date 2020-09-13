package com.jamitek.photosapp.model

data class RemoteLibraryScanStatus (
    val state: String,
    val mediaFilesDetected: Int,
    val filesMoved: Int,
    val filesRemoved: Int,
    val newFiles: Int,
    val filesToProcess: Int,
    val filesProcessed: Int
)