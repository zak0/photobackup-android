package com.jamitek.photosapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.locallibrary.LocalLibraryRepository

class LocalLibraryViewModel(private val repository: LocalLibraryRepository) : ViewModel() {

    val libraryStatus = repository.status

    fun scan() {
        repository.scan()
    }

    fun backup() {
        repository.backup()
    }

    fun onCameraDirChanged(newCameraDir: Uri) {
        repository.cameraDirUriString = newCameraDir.toString()
    }
}