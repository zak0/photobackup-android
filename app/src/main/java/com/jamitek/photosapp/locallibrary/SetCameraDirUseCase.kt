package com.jamitek.photosapp.locallibrary

import android.net.Uri

class SetCameraDirUseCase(
    private val repository: LocalCameraRepository
) {

    fun onCameraDirChanged(newCameraDir: Uri) {
        repository.cameraDirUriString = newCameraDir.toString()
    }

}