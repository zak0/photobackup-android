package com.jamitek.photosapp.locallibrary

import android.net.Uri
import androidx.lifecycle.ViewModel

class LocalLibraryViewModel(private val repository: LocalLibraryRepository) : ViewModel() {

    val libraryStatus = repository.status

    fun scan() {
        repository.scan()
    }

    fun onCameraDirChanged(newCameraDir: Uri) {
        repository.cameraDirUriString = newCameraDir.toString()
    }
}