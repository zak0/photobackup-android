package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.database.KeyValueStore

class SettingsViewModel(private val keyValueStore: KeyValueStore) : ViewModel() {

    val cameraDirUriString: String?
        get() = keyValueStore.getString(KeyValueStore.KEY_CAMERA_DIR_URI)

}