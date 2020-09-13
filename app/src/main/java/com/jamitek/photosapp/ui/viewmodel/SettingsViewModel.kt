package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository

class SettingsViewModel(
    private val keyValueStore: KeyValueStore,
    private val adminRepo: RemoteLibraryAdminRepository
) : ViewModel() {

    val cameraDirUriString: String?
        get() = keyValueStore.getString(KeyValueStore.KEY_CAMERA_DIR_URI)

    val remoteLibraryScanStatus = adminRepo.libraryScanStatus

    fun initRemoteLibraryScan() {
        adminRepo.initLibraryScan()
    }

    fun refreshRemoteLibraryScanStatus() {
        adminRepo.refreshScanStatus()
    }

}