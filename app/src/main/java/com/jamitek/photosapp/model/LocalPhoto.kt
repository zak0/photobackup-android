package com.jamitek.photosapp.model

/**
 * Photo that exists locally on this device's mass storage.
 */
data class LocalPhoto(
    var id: Int,
    var fileName: String,
    var fileSize: Long,
    var hash: String,
    var dateTimeOriginal: String,
    var uriString: String,
    var thumbnailUriString: String,
    var status: String
)
