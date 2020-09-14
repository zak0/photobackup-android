package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.networking.ServerConfigRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository

class SettingsViewModel(
    private val keyValueStore: KeyValueStore,
    private val adminRepo: RemoteLibraryAdminRepository,
    private val serverConfigRepo: ServerConfigRepository
) : ViewModel() {

    val cameraDirUriString: String?
        get() = keyValueStore.getString(KeyValueStore.KEY_CAMERA_DIR_URI)

    val remoteLibraryScanStatus = adminRepo.libraryScanStatus

    val serverAddressIsSet
        get() = serverConfigRepo.urlIsSet
    val currentServerAddress
        get() = serverConfigRepo.baseUrl

    fun initRemoteLibraryScan() {
        adminRepo.initLibraryScan()
    }

    fun refreshRemoteLibraryScanStatus() {
        adminRepo.refreshScanStatus()
    }

    fun addServer(serverUrl: String) {
        serverConfigRepo.addServerAddress(serverUrl)
        serverConfigRepo.selectAddress(serverUrl)
    }

    fun clearServers() {
        serverConfigRepo.allUrls.forEach { serverConfigRepo.removeServerAddress(it) }
    }

}