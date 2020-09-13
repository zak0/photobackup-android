package com.jamitek.photosapp.networking

import android.util.Log
import com.jamitek.photosapp.model.LocalMedia
import com.jamitek.photosapp.model.RemoteLibraryScanStatus
import com.jamitek.photosapp.model.RemoteMedia
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MediaSerializer {

    companion object {
        private const val TAG = "MediaSerializer"
    }

    fun localMediaToMetaDataPostBody(localMedia: LocalMedia): String {
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

    fun parseRemoteMediasJson(json: String): List<RemoteMedia> {
        val photos = ArrayList<RemoteMedia>()

        try {
            val files = JSONArray(json)
            for (i in 0 until files.length()) {
                val file = files.getJSONObject(i)
                val photo = parseRemoteMediaJson(file)
                photos.add(photo)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Response parsing failed: ", e)
        }

        return photos
    }

    fun parseRemoteMediaJson(jsonString: String): RemoteMedia {
        return parseRemoteMediaJson(JSONObject(jsonString))
    }

    fun parseRemoteMediaJson(json: JSONObject): RemoteMedia {
        return RemoteMedia(
            json.getInt("id"),
            json.getString("fileName"),
            json.getLong("fileSize"),
            "dirpath goes here",
            json.getString("checksum"),
            json.getString("dateTimeOriginal"),
            json.getString("status")
        )
    }

    fun parseRemoteLibraryScanStatusJson(jsonString: String): RemoteLibraryScanStatus {
        val json = JSONObject(jsonString)
        return RemoteLibraryScanStatus(
            state = json.getString("state"),
            mediaFilesDetected = json.getInt("mediaFilesDetected"),
            filesMoved = json.getInt("filesMoved"),
            filesRemoved = json.getInt("filesRemoved"),
            newFiles = json.getInt("newFiles"),
            filesToProcess = json.getInt("filesToProcess"),
            filesProcessed = json.getInt("filesProcessed")
        )
    }
}