package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.networking.ServerSetupSettingsItemKey
import com.jamitek.photosapp.networking.ServerConfigUseCase

class ServerSetupViewModel(private val useCase: ServerConfigUseCase) : ViewModel() {
    val uiEvent = useCase.uiEvent
    val items = useCase.items

    fun onItemClicked(key: ServerSetupSettingsItemKey) {
        useCase.onItemClicked(key)
    }
}
