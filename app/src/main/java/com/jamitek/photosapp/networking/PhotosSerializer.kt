package com.jamitek.photosapp.networking

import com.jamitek.photosapp.model.Photo
import org.json.JSONObject

object PhotosSerializer {
    fun getPhotoMetaRequest(photo: Photo): String {
        return JSONObject()
            .put("fileName", photo.fileName)
            .put("fileSize", photo.fileSize)
            .put("hash", photo.hash)
            .toString()
    }
}