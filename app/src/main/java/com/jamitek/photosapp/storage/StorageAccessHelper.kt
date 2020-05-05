package com.jamitek.photosapp.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

object StorageAccessHelper {

    const val TAG = "StorageAccessHelper"
    const val REQUEST_CODE_SET_CAMERA_DIR = 100
    val SUPPORTED_EXTENSIONS = listOf("png", "jpg")

    fun promptRootDirSelection(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, REQUEST_CODE_SET_CAMERA_DIR)
    }

    fun iterateCameraDir(context: Context, uriString: String) {
        DocumentFile.fromTreeUri(context, Uri.parse(uriString))?.also { docFile ->
            if (!docFile.isDirectory) {
                throw IllegalStateException("Selected camera directory is not a directory.")
            }

            docFile.listFiles().forEach { childDocFile ->
                val fileName = childDocFile.name ?: ""

                // Only accept image files
                if (fileName.split(".").last().toLowerCase(Locale.ROOT) in SUPPORTED_EXTENSIONS) {
                    val fileSize = childDocFile.length()
                    val digest = calculateMd5ForFile(context, childDocFile)

                    Log.d(TAG, "filename: $fileName, filesize: $fileSize, digest: $digest")
                }
            }
        }
    }

    private fun calculateMd5ForFile(context: Context, file: DocumentFile): String? {
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(32768)
        var output: String? = null

        context.contentResolver.openInputStream(file.uri)?.also { stream ->
            var readBytes = stream.read(buffer)
            while (readBytes > 0) {
                digest.update(buffer, 0, readBytes)
                readBytes = stream.read(buffer)
            }

            val md5Sum = digest.digest()
            val bigInt = BigInteger(1, md5Sum)
            output = bigInt.toString(16)
            output = String.format("%32s", output).replace(" ", "0")
        }

        return output
    }

}
