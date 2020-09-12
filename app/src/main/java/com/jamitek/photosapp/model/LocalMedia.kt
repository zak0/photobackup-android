package com.jamitek.photosapp.model

data class LocalMedia(
    var id: Int,
    var fileName: String,
    var uri: String,
    var fileSize: Long,
    var checksum: String,
    var uploaded: Boolean
)