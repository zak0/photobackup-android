package com.jamitek.photosapp.database

data class LocalMedia (
    var id: Int,
    var uri: String,
    var fileSize: Long,
    var checksum: String,
    var uploaded: Boolean
)
