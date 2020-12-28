package com.jamitek.photosapp.api

import android.content.Context
import com.jamitek.photosapp.database.KeyValueStore
import java.util.*

class ServerConfigStore(context: Context) : KeyValueStore(context) {

    private companion object {
        const val KEY_URL = "server.config.url"
        const val KEY_USERNAME = "server.config.username"
        const val KEY_TOKEN = "server.config.token"
    }

    var serverUrl: String?
        get() = getString(KEY_URL)
        set(value) = putString(KEY_URL, value)

    val authHeader: Pair<String, String>?
        get() = authToken?.let { "Authorization" to "Basic $it" }

    var username: String?
        get() = getString(KEY_USERNAME)
        private set(value) = putString(KEY_USERNAME, value)

    val urlIsSet: Boolean
        get() {
            // Valid URL contains at least "http://" and starts with "http"
            return baseUrl.length > 7 && baseUrl.startsWith("http")
        }

    val baseUrl: String
        get() {
            var string = serverUrl ?: ""

            // Ensure that the URL contains the trailing forward slash.
            // It's added here so that it's not necessary to be added by the user
            if (string.isNotEmpty() && string.lastOrNull() != '/') {
                string += "/"
            }

            return string
        }

    private var authToken: String?
        get() = getString(KEY_TOKEN)
        set(value) = putString(KEY_TOKEN, value)

    private val configChangeListeners = HashMap<String, () -> Unit>()

    fun unsubscribeFromServerConfigChanges(tag: String) {
        configChangeListeners.remove(tag)
    }

    fun subscribeToServerConfigChanges(tag: String, block: () -> Unit) {
        configChangeListeners[tag] = block
    }

    fun setCredentials(username: String, password: String) {
        this.username = username

        val authString = "$username:$password"
        authToken = Base64.getEncoder().encodeToString(authString.toByteArray())

        notifyConfigChangeListeners()
    }

    private fun notifyConfigChangeListeners() {
        configChangeListeners.forEach {
            val callback = it.value
            callback()
        }
    }
}