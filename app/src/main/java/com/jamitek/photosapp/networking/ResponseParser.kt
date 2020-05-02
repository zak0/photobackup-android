package com.jamitek.photosapp.networking

import android.util.Log
import com.jamitek.photosapp.model.Photo
import org.json.JSONException
import org.json.JSONObject

object ResponseParser {
    private const val TAG = "ResponseParser"

    fun parsePhotosJson(json: String): List<Photo> {
        val photos = ArrayList<Photo>()

        try {
            val root = JSONObject(json)
            val files = root.getJSONArray("files")
            for (i in 0 until files.length()) {
                val file = files.getJSONObject(i)
                val photo = Photo(
                    file.getInt("id"),
                    file.getString("fileName"),
                    file.getLong("fileSize"),
                    "dirpath goes here",
                    file.getString("hash"),
                    file.getString("dateTimeOriginal"),
                    file.getString("status")
                )
                photos.add(photo)
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Response parsing failed: ", e)
        }

        return photos
    }
}
