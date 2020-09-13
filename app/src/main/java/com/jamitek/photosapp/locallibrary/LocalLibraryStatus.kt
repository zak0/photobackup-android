package com.jamitek.photosapp.locallibrary

data class LocalLibraryStatus(
    val isUploading: Boolean,
    val isScanning: Boolean,
    val localFilesCount: Int,
    val waitingForBackupCount: Int
)