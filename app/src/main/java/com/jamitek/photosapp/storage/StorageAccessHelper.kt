package com.jamitek.photosapp.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import java.io.InputStream

class StorageAccessHelper(private val context: Context) {

    companion object {
        private const val TAG = "StorageAccessHelper"
        const val REQUEST_CODE_SET_CAMERA_DIR = 100
        const val REQUEST_CODE_SET_LOCAL_FOLDERS_ROOT_DIR = 200
        val SUPPORTED_EXTENSIONS = listOf("png", "jpg") // TODO Add video files

        fun promptCameraDirSelection(activity: Activity) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            activity.startActivityForResult(intent, REQUEST_CODE_SET_CAMERA_DIR)
        }

        fun promptLocalFoldersRootDirSelection(activity: Activity) {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            activity.startActivityForResult(intent, REQUEST_CODE_SET_LOCAL_FOLDERS_ROOT_DIR)
        }
    }

    fun getFileAsByteArray(uriString: String): ByteArray? {
        var bytes: ByteArray? = null
        var stream: InputStream? = null
        try {
            stream = context.contentResolver.openInputStream(Uri.parse(uriString))
            bytes = stream?.readBytes()
        } catch (e: Exception) {
            Log.e(TAG, "Could not read file contents: ", e)
        } finally {
            stream?.close()
        }

        return bytes
    }

}
