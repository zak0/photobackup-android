package com.jamitek.photosapp.networking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.SettingsItem

class ServerConfigUseCase(
    private val repo: ServerConfigRepository
) {

    val items: LiveData<List<SettingsItem>> = MutableLiveData<List<SettingsItem>>(buildSettings())

    private fun buildSettings(): List<SettingsItem> = ServerConfigSettingsItemKey.values().map { SettingsItem(it) }

}