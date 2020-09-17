package com.jamitek.photosapp.locallibrary

data class LocalCameraLibraryStatus(
    val isUploading: Boolean,
    val isScanning: Boolean,
    val localFilesCount: Int,
    val waitingForBackupCount: Int
)