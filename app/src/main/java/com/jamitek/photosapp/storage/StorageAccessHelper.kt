package com.jamitek.photosapp.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jamitek.photosapp.model.Photo

object StorageAccessHelper {

    const val TAG = "StorageAccessHelper"
    const val REQUEST_CODE_SET_CAMERA_DIR = 100
    val SUPPORTED_EXTENSIONS = listOf("png", "jpg") // TODO Add video files

    fun promptRootDirSelection(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, REQUEST_CODE_SET_CAMERA_DIR)
    }

    fun getPhotoAsByteArray(context: Context, photo: Photo): ByteArray? {
        return photo.localUriString?.let { uriString ->
            context.contentResolver.openInputStream(Uri.parse(uriString))?.readBytes()
        }
    }

}
