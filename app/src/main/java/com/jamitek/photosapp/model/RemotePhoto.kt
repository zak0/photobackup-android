package com.jamitek.photosapp.model

/**
 * Photo that exists on the server.
 */
data class RemotePhoto(
    var id: Int,
    var fileName: String,
    var fileSize: Long,
    var dirPath: String,
    var hash: String,
    var dateTimeOriginal: String,
    var status: String
)
