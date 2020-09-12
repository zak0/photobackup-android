package com.jamitek.photosapp.networking

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

object UrlHelper {
    val baseUrl = "http://192.168.1.46:3001/"
    fun thumbnailUrl(photoIdOnServer: Int) = "${baseUrl}media/$photoIdOnServer/thumbnail"
    fun photoUrl(photoIdOnServer: Int) = "${baseUrl}media/$photoIdOnServer/file"
    fun authorizedGlideUrl(url: String) = GlideUrl(
        url,
        LazyHeaders.Builder()
            .addHeader("Authorization", "Basic amFha2tvYWRtaW46U2FsYWluZW5TYW5hMTMyNCFA")
            .build()
    )
}
