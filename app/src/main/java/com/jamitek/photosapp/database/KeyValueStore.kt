package com.jamitek.photosapp.database

import android.content.Context

class KeyValueStore(context: Context) {

    companion object {
        private const val PREFS_NAME = "photosapp.prefs"
        const val KEY_CAMERA_DIR_URI = "camera.dir.uri"
        const val KEY_SERVER_ALL_ADDRESSES = "server.address.all"
        const val KEY_SERVER_SELECTED_ADDRESS = "server.address.selected"
        const val KEY_USERNAME = "server.auth.username"
        const val KEY_TOKEN = "server.auth.token"
    }

    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPrefs.getString(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        sharedPrefs.edit().putString(key, value).apply()
    }

    fun clear(key: String) {
        sharedPrefs.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }
}
