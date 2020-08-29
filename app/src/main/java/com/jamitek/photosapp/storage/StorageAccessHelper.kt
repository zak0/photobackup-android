package com.jamitek.photosapp.storage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.jamitek.photosapp.DateUtil
import com.jamitek.photosapp.RemoteLibraryRepository
import com.jamitek.photosapp.model.Photo
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import kotlin.collections.ArrayList

class StorageAccessHelper {

    companion object {
        const val TAG = "StorageAccessHelper"
        const val REQUEST_CODE_SET_CAMERA_DIR = 100
        val SUPPORTED_EXTENSIONS = listOf("png", "jpg")
    }

    fun promptRootDirSelection(activity: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        activity.startActivityForResult(intent, REQUEST_CODE_SET_CAMERA_DIR)
    }

    suspend fun iterateCameraDir(context: Context, uriString: String) {
        DocumentFile.fromTreeUri(context, Uri.parse(uriString))?.also { docFile ->
            if (!docFile.isDirectory) {
                throw IllegalStateException("Selected camera directory is not a directory.")
            }

            val localPhotos = ArrayList<Photo>()

            docFile.listFiles().forEach { childDocFile ->
                val fileName = childDocFile.name ?: ""

                // Only accept image files
                if (fileName.split(".").last().toLowerCase(Locale.ROOT) in SUPPORTED_EXTENSIONS) {
                    val fileSize = childDocFile.length()
                    val digest = calculateMd5ForFile(context, childDocFile)

                    // TODO Check if local photo record already exists for this photo (based on
                    //  its file size and hash), and only if not, then read necessary info and
                    //  add it into the metadata database.
                    val fileUriString = childDocFile.uri.toString()

                    val photo = Photo(
                        null,
                        null,
                        fileName,
                        fileSize,
                        null,
                        fileUriString,
                        null,
                        digest,
                        DateUtil.EPOCH_EXIF, // 1970:01:01 00:00:00
                        null
                    )

                    localPhotos.add(photo)
                    Log.d(TAG, "filename: $fileName, filesize: $fileSize, digest: $digest")
                }
            }

            //RemoteLibraryRepository.onLocalPhotosLoaded(context, localPhotos)
        }
    }

    private fun calculateMd5ForFile(context: Context, file: DocumentFile): String {
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(32768)

        return context.contentResolver.openInputStream(file.uri)?.let { stream ->
            var readBytes = stream.read(buffer)
            while (readBytes > 0) {
                digest.update(buffer, 0, readBytes)
                readBytes = stream.read(buffer)
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

    fun getPhotoAsByteArray(context: Context, photo: Photo): ByteArray? {
        return photo.localUriString?.let { uriString ->
            context.contentResolver.openInputStream(Uri.parse(uriString))?.readBytes()
        }
    }

}
