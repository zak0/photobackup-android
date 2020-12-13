package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.api.ServerSetupSettingsItemKey
import com.jamitek.photosapp.api.ServerConfigUseCase

class ServerSetupViewModel(private val useCase: ServerConfigUseCase) : ViewModel() {
    val newConfig = useCase.newConfig
    val uiEvent = useCase.uiEvent
    val items = useCase.items

    fun onItemClicked(key: ServerSetupSettingsItemKey) {
        useCase.onItemClicked(key)
    }

    fun onServerUrlSet(serverUrl: String) {
        useCase.newServerUrlSet(serverUrl)
    }

    fun onUsernameSet(username: String) {
        useCase.newUsername(username)
    }

    fun onPasswordSet(password: String) {
        useCase.newPassword(password)
    }

    fun onSaveConfig() {
        useCase.saveServerConfig()
    }
}
