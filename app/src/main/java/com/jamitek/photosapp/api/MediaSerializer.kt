package com.jamitek.photosapp.api

import android.util.Log
import com.jamitek.photosapp.api.model.ApiMediaFile
import com.jamitek.photosapp.model.LocalMedia
import com.jamitek.photosapp.model.RemoteLibraryScanStatus
import com.jamitek.photosapp.model.RemoteMedia
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
        val jsonn = """
                {
                    "id": 1,
                    "fileName": "IMG_20190606_202357.jpg",
                    "fileSize": 330349,
                    "dirPath": "/photosapp/media/2019-2020_pixel3",
                    "checksum": "6eff5c285dceb0777fd902ee99ddf72b",
                    "dateTimeOriginal": "2019:06:06 20:23:57",
                    "status": "ready"
                }
        """.trimIndent()

        val apiMediaFile = Json { ignoreUnknownKeys = true }.decodeFromString<ApiMediaFile>(jsonn)

        return RemoteMedia(
            json.getInt("id"),
            json.getString("fileName"),
            json.getLong("fileSize"),
            json.getString("dirPath"),
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