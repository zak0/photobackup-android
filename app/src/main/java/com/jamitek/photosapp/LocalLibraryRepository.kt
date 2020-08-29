package com.jamitek.photosapp

import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.KeyValueStore.Companion.KEY_CAMERA_DIR_URI

class LocalLibraryRepository(private val keyValueStore: KeyValueStore) {

    var cameraDirUriString: String?
        get() = keyValueStore.getString(KEY_CAMERA_DIR_URI)
        set(value) {
            keyValueStore.getString(KEY_CAMERA_DIR_URI, value)
        }

}