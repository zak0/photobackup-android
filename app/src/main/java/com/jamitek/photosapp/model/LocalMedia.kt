package com.jamitek.photosapp.model

data class LocalMedia(
    var id: Int,
    var type: String,
    var fileName: String,
    var directoryUri: String,
    var uri: String,
    var fileSize: Long,
    var checksum: String,
    var uploaded: Boolean
) : DisplayableMedia
