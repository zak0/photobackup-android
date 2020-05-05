package com.jamitek.photosapp.networking

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

object UrlHelper {
    val baseUrl = "http://192.168.1.105:3000/"
    fun thumbnailUrl(photoId: Int) = "${baseUrl}media/$photoId/thumbnail"
    fun photoUrl(photoId: Int) = "${baseUrl}media/$photoId/file"
    fun authorizedGlideUrl(url: String) = GlideUrl(
        url,
        LazyHeaders.Builder()
            .addHeader("Authorization", "Basic amFha2tvYWRtaW46U2FsYWluZW5TYW5hMTMyNCFA")
            .build()
    )
}
