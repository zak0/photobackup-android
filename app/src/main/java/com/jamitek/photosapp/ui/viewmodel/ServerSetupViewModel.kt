package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.networking.ServerConfigUseCase

class ServerSetupViewModel(private val useCase: ServerConfigUseCase) : ViewModel() {
    val items = useCase.items
}
