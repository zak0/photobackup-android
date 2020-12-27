package com.jamitek.photosapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.locallibrary.SetCameraDirUseCase

class RootViewModel(private val useCase: SetCameraDirUseCase) : ViewModel() {
    fun onCameraDirChanged(newCameraDir: Uri) {
        useCase.onCameraDirChanged(newCameraDir)
    }
}