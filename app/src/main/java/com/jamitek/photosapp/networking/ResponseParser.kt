package com.jamitek.photosapp.networking

import android.util.Log
import com.jamitek.photosapp.model.Photo
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ResponseParser {

    companion object {
        private const val TAG = "ResponseParser"
    }

    fun parsePhotosJson(json: String): List<Photo> {
        val photos = ArrayList<Photo>()

        try {
            val files = JSONArray(json)
            for (i in 0 until files.length()) {
                val file = files.getJSONObject(i)
                val photo = parsePhotoJson(file)
                photos.add(photo)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Response parsing failed: ", e)
        }

        return photos
    }

    fun parsePhotoJson(jsonString: String): Photo {
        return parsePhotoJson(JSONObject(jsonString))
    }

    fun parsePhotoJson(json: JSONObject): Photo {
        return Photo(
            null, // local id
            json.getInt("id"),
            json.getString("fileName"),
            json.getLong("fileSize"),
            "dirpath goes here",
            null,
            null,
            json.getString("checksum"),
            json.getString("dateTimeOriginal"),
            json.getString("status")
        )
    }
}
