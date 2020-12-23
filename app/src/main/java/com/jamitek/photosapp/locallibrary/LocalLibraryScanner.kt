package com.jamitek.photosapp.locallibrary

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.jamitek.photosapp.model.LocalMedia
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
     * With each detected media file, calls [onMediaFile] callback. Parameters of the callback are
     * [LocalMedia] of the discovered media file, and [DocumentFile] of the containing directory.
     */
    fun iterateCameraDir(cameraDirUri: String, onMediaFile: (LocalMedia, DocumentFile) -> Unit) {
        DocumentFile.fromTreeUri(context, Uri.parse(cameraDirUri))?.also { docFile ->
            if (!docFile.isDirectory) {
                throw IllegalStateException("Selected camera directory is not a directory.")
            }

            iterateDirectory(docFile, true, onMediaFile)
        }
    }

    fun iterateLocalFolders(
        localFoldersRootUri: String,
        onMediaFile: (LocalMedia, DocumentFile) -> Unit
    ) {
        DocumentFile.fromTreeUri(context, Uri.parse(localFoldersRootUri))?.also { docFile ->
            if (!docFile.isDirectory) {
                throw IllegalStateException("Selected local folders root directory is not a directory.")
            }

            iterateDirectory(docFile, false, onMediaFile)
        }
    }

    /**
     * Scans a directory and all subdirectories for supported media files. If a supported file is encountered, calls
     * [onMediaFile] with it as the parameter. If a directory is encountered, calls this same
     * method again to scan that directory.
     *
     * When [calculateChecksum] is set to true, calculates MD5 hash for media files and stores it in
     * [LocalMedia.checksum], otherwise sets [LocalMedia.checksum] to an empty string ("").
     *
     * Ignores folders (and their subfolders) that contain a ".nomedia" file.
     */
    private fun iterateDirectory(
        directory: DocumentFile,
        calculateChecksum: Boolean,
        onMediaFile: (LocalMedia, DocumentFile) -> Unit
    ) {

        val dirUriString = directory.uri.toString()
        val dirHasNoMedia = directory.findFile(".nomedia") != null

        if (!dirHasNoMedia) {
            directory.listFiles().forEach { childDocFile ->
                val fileName = childDocFile.name ?: ""

                // Iterate subfolders as well
                if (childDocFile.isDirectory) {
                    iterateDirectory(childDocFile, calculateChecksum, onMediaFile)
                }

                // Only accept media files
                val fileExtension = fileName.split(".").last().toLowerCase(Locale.ROOT)
                val isPicture = fileExtension in StorageAccessHelper.SUPPORTED_PICTURE_EXTENSIONS
                val isVideo = fileExtension in StorageAccessHelper.SUPPORTED_VIDEO_EXTENSIONS
                if (isPicture || isVideo) {
                    val fileSize = childDocFile.length()
                    val digest =
                        if (calculateChecksum) calculateMd5ForFile(context, childDocFile) else ""
                    val fileUriString = childDocFile.uri.toString()
                    onMediaFile(
                        LocalMedia(
                            -1,
                            if (isPicture) "Picture" else if (isVideo) "Video" else error("Media must have a type!"),
                            fileName,
                            dirUriString,
                            fileUriString,
                            fileSize,
                            digest,
                            false
                        ),
                        directory
                    )
                }
            }
        } else {
            Log.d(
                TAG,
                "Directory '${directory.name}' contains '.nomedia' file. Scanning for it and its subdirectories is skipped."
            )
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