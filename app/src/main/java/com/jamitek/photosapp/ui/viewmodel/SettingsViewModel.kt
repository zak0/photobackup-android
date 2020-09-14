package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.networking.UrlRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository

class SettingsViewModel(
    private val keyValueStore: KeyValueStore,
    private val adminRepo: RemoteLibraryAdminRepository,
    private val urlRepo: UrlRepository
) : ViewModel() {

    val cameraDirUriString: String?
        get() = keyValueStore.getString(KeyValueStore.KEY_CAMERA_DIR_URI)

    val remoteLibraryScanStatus = adminRepo.libraryScanStatus

    val serverAddressIsSet
        get() = urlRepo.urlIsSet
    val currentServerAddress
        get() = urlRepo.baseUrl

    fun initRemoteLibraryScan() {
        adminRepo.initLibraryScan()
    }

    fun refreshRemoteLibraryScanStatus() {
        adminRepo.refreshScanStatus()
    }

    fun addServer(serverUrl: String) {
        urlRepo.addServerAddress(serverUrl)
        urlRepo.selectAddress(serverUrl)
    }

    fun clearServers() {
        urlRepo.allUrls.forEach { urlRepo.removeServerAddress(it) }
    }

}