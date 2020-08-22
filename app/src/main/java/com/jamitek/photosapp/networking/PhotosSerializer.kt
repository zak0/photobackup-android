package com.jamitek.photosapp.networking

import com.jamitek.photosapp.model.Photo
import org.json.JSONObject

object PhotosSerializer {
    fun getPhotoMetaRequest(photo: Photo): String {
        return JSONObject()
            .put("id", -1)
            .put("dirPath", "")
            .put("dateTimeOriginal", "")
            .put("status", "")
            .put("fileName", photo.fileName)
            .put("fileSize", photo.fileSize)
            .put("checksum", photo.hash)
            .toString()
    }
}