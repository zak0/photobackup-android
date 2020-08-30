package com.jamitek.photosapp.locallibrary

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.jamitek.photosapp.database.LocalMedia
import com.jamitek.photosapp.storage.StorageAccessHelper
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*

class LocalLibraryScanner(private val context: Context) {

    companion object {
        private const val TAG = "LocalLibScanner"
    }

    /**
     * Iterates the user set camera directory and all its sub-directories for supported media files.
     * With each detected media file, calls [onMediaFile] callback.
     */
    fun iterateCameraDir(cameraDirUri: String, onMediaFile: (LocalMedia) -> Unit) {
        DocumentFile.fromTreeUri(context, Uri.parse(cameraDirUri))?.also { docFile ->
            if (!docFile.isDirectory) {
                throw IllegalStateException("Selected camera directory is not a directory.")
            }

            iterateDirectory(docFile, onMediaFile)
        }
    }

    /**
     * Scans a directory and all subdirectories for supported media files. If a supported file is encountered, calls
     * [onMediaFile] with it as the parameter. If a directory is encountered, calls this same
     * method again to scan that directory.
     */
    private fun iterateDirectory(directory: DocumentFile, onMediaFile: (LocalMedia) -> Unit) {
        directory.listFiles().forEach { childDocFile ->
            val fileName = childDocFile.name ?: ""

            // Iterate subfolders as well
            if (childDocFile.isDirectory) {
                iterateDirectory(childDocFile, onMediaFile)
            }

            // Only accept media files
            if (fileName.split(".").last()
                    .toLowerCase(Locale.ROOT) in StorageAccessHelper.SUPPORTED_EXTENSIONS
            ) {
                val fileSize = childDocFile.length()
                val digest = calculateMd5ForFile(context, childDocFile)
                val fileUriString = childDocFile.uri.toString()
                onMediaFile(LocalMedia(-1, fileName, fileUriString, fileSize, digest, false))
                Log.d(
                    StorageAccessHelper.TAG,
                    "filename: $fileName, filesize: $fileSize, digest: $digest"
                )
            }
        }
    }

    private fun calculateMd5ForFile(context: Context, file: DocumentFile): String {
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(32768)

        return context.contentResolver.openInputStream(file.uri)?.let { stream ->
            var readBytes = stream.read(buffer)
            try {
                while (readBytes > 0) {
                    digest.update(buffer, 0, readBytes)
                    readBytes = stream.read(buffer)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to calculate MD5 hash for ${file.name}.", e)
            } finally {
                stream.close()
            }

            val md5Sum = digest.digest()
            val bigInt = BigInteger(1, md5Sum)
            val asString = bigInt.toString(16)
            String.format("%32s", asString).replace(" ", "0")
        } ?: run {
            throw IllegalStateException("Failed to calculate MD5 hash for ${file.name}.")
        }
    }

    fun getFileLastModifiedDate(context: Context, uriString: String): Date? {
        return DocumentFile.fromSingleUri(context, Uri.parse(uriString))?.let {
            Date(it.lastModified())
        }
    }

}