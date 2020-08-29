package com.jamitek.photosapp.networking

import com.jamitek.photosapp.database.LocalMedia
import org.json.JSONObject

class PhotosSerializer {
    fun getPhotoMetaRequest(localMedia: LocalMedia): String {
        return JSONObject()
            .put("id", -1)
            .put("dirPath", "")
            .put("dateTimeOriginal", "")
            .put("status", "")
            .put("fileName", localMedia.fileName)
            .put("fileSize", localMedia.fileSize)
            .put("checksum", localMedia.checksum)
            .toString()
    }
}