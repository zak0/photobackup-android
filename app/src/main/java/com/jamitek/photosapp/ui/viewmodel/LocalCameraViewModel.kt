package com.jamitek.photosapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.locallibrary.LocalCameraRepository

// TODO Remove? If yes, move current onCameraDirChanged elsewhere.
class LocalCameraViewModel(private val repository: LocalCameraRepository) : ViewModel() {
    fun onCameraDirChanged(newCameraDir: Uri) {
        repository.cameraDirUriString = newCameraDir.toString()
    }
}