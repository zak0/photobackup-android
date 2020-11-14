package com.jamitek.photosapp.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.ui.ServerSetupScreenEvent

class ServerConfigUseCase(
    private val repo: ServerConfigRepository
) {

    private val mutableUiEvent = MutableLiveData<Event<ServerSetupScreenEvent>>()
    val uiEvent: LiveData<Event<ServerSetupScreenEvent>> = mutableUiEvent
    val items: LiveData<List<SettingsItem>> = MutableLiveData<List<SettingsItem>>(buildSettings())

    fun onItemClicked(key: ServerSetupSettingsItemKey) {
        when (key) {
            ServerSetupSettingsItemKey.ItemAddress -> emitScreenEvent(ServerSetupScreenEvent.ShowServerAddressDialog)
            ServerSetupSettingsItemKey.ItemUsername -> emitScreenEvent(ServerSetupScreenEvent.ShowUsernameDialog)
            ServerSetupSettingsItemKey.ItemPassword -> emitScreenEvent(ServerSetupScreenEvent.ShowPasswordDialog)
            else -> Unit
        }
    }

    private fun emitScreenEvent(event: ServerSetupScreenEvent) {
        mutableUiEvent.value = Event(event)
    }

    /**
     * TODO Write me
     */
    private fun buildSettings(): List<SettingsItem> =
        ServerSetupSettingsItemKey.values().map { SettingsItem(it) }

}