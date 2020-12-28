package com.jamitek.photosapp.database

import android.content.Context

open class KeyValueStore(context: Context) {

    companion object {
        private const val PREFS_NAME = "photosapp.prefs"
        const val KEY_CAMERA_DIR_URI = "camera.dir.uri"
        const val KEY_LOCAL_FOLDERS_ROOT_URI = "local.folders.root.uri"
    }

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPrefs.getString(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPrefs.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPrefs.edit().putBoolean(key, value).apply()
    }

    fun clear(key: String) {
        sharedPrefs.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }
}
