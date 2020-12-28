package com.jamitek.photosapp.api

class ServerConfigRepository(private val serverConfigStore: ServerConfigStore) {

    val urlIsSet: Boolean
        get() = serverConfigStore.urlIsSet

    val baseUrl: String
        get() = serverConfigStore.baseUrl

    val authHeader: Pair<String, String>?
        get() = serverConfigStore.authHeader

    fun thumbnailUrl(mediaIdOnServer: Int) = "${baseUrl}media/$mediaIdOnServer/thumbnail"
    fun mediaUrl(mediaIdOnServer: Int) = "${baseUrl}media/$mediaIdOnServer/file"

    fun setServerUrl(url: String) {
        serverConfigStore.serverUrl = url
    }

    fun setCredentials(username: String, password: String) {
        serverConfigStore.setCredentials(username, password)
    }

}
