package com.jamitek.photosapp.database

import android.content.Context
import android.content.SharedPreferences
import com.jamitek.photosapp.PhotosApplication

object SharedPrefsPersistence {

    private const val PREFS_NAME = "sharedprefspersistence"
    private const val KEY_CAMERA_DIR_URI = "camera_dir_uri"

    private val sharedPrefs: SharedPreferences by lazy {
        PhotosApplication.INSTANCE.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE
        )
    }

    var cameraDirUriString: String?
        get() {
            return sharedPrefs.getString(KEY_CAMERA_DIR_URI, null)
        }
        set(value) {
            sharedPrefs.edit().putString(KEY_CAMERA_DIR_URI, value).apply()
        }
}
