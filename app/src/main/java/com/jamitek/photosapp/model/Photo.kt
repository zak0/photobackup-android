package com.jamitek.photosapp.model

data class Photo(
    var id: Int,
    var fileName: String,
    var fileSize: Long,
    var dirPath: String,
    var hash: String,
    var dateTimeOriginal: String,
    var status: String
)
