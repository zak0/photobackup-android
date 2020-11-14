package com.jamitek.photosapp.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.ui.ServerSetupScreenEvent

class ServerConfigUseCase(
    private val repo: ServerConfigRepository
) {

    private val mutableNewConfig =
        MutableLiveData<Triple<String?, String?, String?>>(Triple(null, null, null))
    val newConfig: LiveData<Triple<String?, String?, String?>> = mutableNewConfig

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

    fun newServerUrlSet(serverUrl: String) {
        mutableNewConfig.value = mutableNewConfig.value!!.copy(first = serverUrl)
    }

    fun newUsername(username: String) {
        mutableNewConfig.value = mutableNewConfig.value!!.copy(second = username)
    }

    fun newPassword(password: String) {
        mutableNewConfig.value = mutableNewConfig.value!!.copy(third = password)
    }

    fun saveServerConfig() {
        // TODO Validate before saving?
        newConfig.value?.also { config ->
            repo.addServerAddress(config.first!!)
            repo.selectAddress(config.first!!)
            repo.setCredentials(config.second!!, config.third!!)
        }

    }

    private fun emitScreenEvent(event: ServerSetupScreenEvent) {
        mutableUiEvent.value = Event(event)
    }

    private fun buildSettings(): List<SettingsItem> = listOf(
        SettingsItem(ServerSetupSettingsItemKey.SectionTitleAddress),
        SettingsItem(ServerSetupSettingsItemKey.ItemAddress) {
            newConfig.value?.first ?: "Not set"
        },
        SettingsItem(ServerSetupSettingsItemKey.SectionTitleCredentials),
        SettingsItem(ServerSetupSettingsItemKey.ItemUsername) {
            newConfig.value?.second ?: "Not set"
        },
        SettingsItem(ServerSetupSettingsItemKey.ItemPassword) {
            newConfig.value?.third?.let { "****************" } ?: "Not set"
        }
    )

}