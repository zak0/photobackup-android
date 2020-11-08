package com.jamitek.photosapp.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.io.InputStream

class StorageAccessHelper(private val context: Context) {

    companion object {
        private const val TAG = "StorageAccessHelper"
        const val REQUEST_CODE_SET_CAMERA_DIR = 100
        val SUPPORTED_EXTENSIONS = listOf("png", "jpg") // TODO Add video files
        val SAF_ACCESS_INTENT = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                    .or(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    .or(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    .or(Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            )
        }

        fun promptCameraDirSelection(activity: Activity) {
            activity.startActivityForResult(SAF_ACCESS_INTENT, REQUEST_CODE_SET_CAMERA_DIR)
        }

        /**
         * Prompts the new permission to keep accessing storage freely through SAF.
         *
         * TODO: This is currently not used, see if this should be used instead.
         */
        fun promptStorageAccess(activity: Activity) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            activity.startActivityForResult(intent, 9002)
        }
    }

    fun treeUriStringToDocFileUriString(treeUriString: String?): String? {
        treeUriString ?: return null

        return DocumentFile.fromTreeUri(context, Uri.parse(treeUriString))?.let { docFile ->
            if (!docFile.isDirectory) {
                throw IllegalStateException("Tree uri root is not a directory.")
            }

            docFile.uri.toString()
        }
    }

    fun uriToDocFile(uriString: String): DocumentFile? {
        return DocumentFile.fromSingleUri(context, Uri.parse(uriString))
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
