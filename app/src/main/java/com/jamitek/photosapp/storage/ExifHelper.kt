package com.jamitek.photosapp.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.IOException

object ExifHelper {

    private const val TAG = "ExifHelper"

    fun getDateTimeOriginal(context: Context, uriString: String): String? {
        return context.contentResolver.openInputStream(Uri.parse(uriString))?.let {
            var exifDate: String? = null
            try {
                exifDate = ExifInterface(it).getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
            } catch (e: IOException) {
                Log.e(TAG, "EXIF extraction failed for local file.", e)
            }
            finally {
                it.close()
            }
            exifDate
        }
    }
}