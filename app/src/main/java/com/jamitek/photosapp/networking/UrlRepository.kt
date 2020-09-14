package com.jamitek.photosapp.networking

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.jamitek.photosapp.database.KeyValueStore
import org.json.JSONArray

class UrlRepository(private val keyValueStore: KeyValueStore) {

    val urlIsSet: Boolean
        get() {
            // Valid URL contains at least "http://" and starts with "http"
            return baseUrl.length > 7 && baseUrl.startsWith("http")
        }

    val baseUrl: String
        get() {
            var string = keyValueStore.getString(KeyValueStore.KEY_SERVER_SELECTED_ADDRESS) ?: ""

            // Ensure that the URL contains the trailing forward slash.
            // It's added here so that it's not necessary to be added by the user
            if (string.isNotEmpty() && string.lastOrNull() != '/') {
                string += "/"
            }

            return string
        }

    val allUrls: Set<String>
        get() {
            val allUrls = HashSet<String>()
            val jsonString =
                keyValueStore.getString(KeyValueStore.KEY_SERVER_ALL_ADDRESSES) ?: "[]"
            val jsonArray = JSONArray(jsonString)
            if (jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    allUrls.add(jsonArray.getString(i))
                }
            }
            return allUrls
        }

    private val urlChangeListeners = HashMap<String, (String) -> Unit>()

    fun unRegisterFromSelectedUrlChanges(tag: String) {
        urlChangeListeners.remove(tag)
    }

    fun registerForSelectedUrlChanges(tag: String, block: (String) -> Unit) {
        urlChangeListeners[tag] = block
    }

    fun selectAddress(address: String) {
        keyValueStore.putString(KeyValueStore.KEY_SERVER_SELECTED_ADDRESS, address)
        urlChangeListeners.forEach {
            val callback = it.value
            callback(address)
        }
    }

    fun addServerAddress(address: String) {
        val newAllUrls = HashSet(allUrls)
        newAllUrls.add(address)
        persistServerAddresses(newAllUrls)
    }

    fun removeServerAddress(address: String) {
        val newAllUrls = HashSet(allUrls)
        newAllUrls.remove(address)
        persistServerAddresses(newAllUrls)
    }

    fun thumbnailUrl(photoIdOnServer: Int) = "${baseUrl}media/$photoIdOnServer/thumbnail"
    fun photoUrl(photoIdOnServer: Int) = "${baseUrl}media/$photoIdOnServer/file"
    fun authorizedGlideUrl(url: String) = GlideUrl(
        url,
        LazyHeaders.Builder()
            .addHeader("Authorization", "Basic amFha2tvYWRtaW46U2FsYWluZW5TYW5hMTMyNCFA")
            .build()
    )

    private fun persistServerAddresses(addresses: Set<String>) {
        val jsonArray = JSONArray()
        addresses.forEach { jsonArray.put(it) }
        keyValueStore.putString(KeyValueStore.KEY_SERVER_ALL_ADDRESSES, jsonArray.toString())
    }
}
