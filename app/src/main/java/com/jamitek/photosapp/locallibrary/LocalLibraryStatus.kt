package com.jamitek.photosapp.locallibrary

data class LocalLibraryStatus(
    val isScanning: Boolean,
    val localFilesCount: Int,
    val waitingForBackupCount: Int
)