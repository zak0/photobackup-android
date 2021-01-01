package com.jamitek.photosapp.database

import android.content.Context
import kotlinx.coroutines.delay

open class KeyValueStore(context: Context) {

    companion object {
        private const val PREFS_NAME = "photosapp.prefs"
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

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPrefs.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPrefs.edit().putLong(key, value).apply()
    }

    fun clear(key: String) {
        sharedPrefs.edit().clear().apply()
    }

    fun remove(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }
}
