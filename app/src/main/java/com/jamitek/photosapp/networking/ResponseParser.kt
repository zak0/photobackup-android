package com.jamitek.photosapp.networking

import org.json.JSONException
import org.json.JSONObject

object ResponseParser {
    fun parsePhotosJson(json: String): JSONObject? {
        return try {
            JSONObject(json)
        } catch (e: JSONException) {
            null
        }
    }
}
