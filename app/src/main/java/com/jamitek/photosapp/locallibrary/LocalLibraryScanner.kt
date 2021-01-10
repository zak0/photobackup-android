package com.jamitek.photosapp.locallibrary

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.jamitek.photosapp.model.LocalMedia
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.util.DateUtil
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
    fun iterateCameraDir(forced: Boolean = false, onMediaFile: (LocalMedia) -> Unit) {

        val startTime = System.currentTimeMillis()

        val projection = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DISPLAY_NAME
        )

        val selection = "${MediaStore.MediaColumns.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf("Camera")

        val resolver = context.contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null,
            null
        )

        val firstPassMediaFiles = ArrayList<LocalMedia>()

        var i = 0
        cursor!!.moveToFirst()

        while (!cursor.isAfterLast) {

            i++
            if (i % 50 == 0) {
                Log.d(TAG, "Progress: $i")
            }

            val stringCol = { colName: String ->
                cursor.getString(cursor.getColumnIndex(colName))
            }

            val longCol = { colName: String ->
                cursor.getLong(cursor.getColumnIndex(colName))
            }

            val bucketName = stringCol(MediaStore.MediaColumns.BUCKET_DISPLAY_NAME)

            // Skip everything except "Camera" bucket
            if (bucketName == "Camera") {

                val fileName = stringCol(MediaStore.MediaColumns.DISPLAY_NAME)
                Log.d(TAG, "Processing '$fileName'")

                val fileExtension = fileName.split(".").last().toLowerCase(Locale.ROOT)
                val isPicture = fileExtension in StorageAccessHelper.SUPPORTED_PICTURE_EXTENSIONS
                val isVideo = fileExtension in StorageAccessHelper.SUPPORTED_VIDEO_EXTENSIONS

                // TODO Try-catch the following at least for first tests with a large library
                if (isPicture || isVideo) {
                    val fileUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        stringCol(MediaStore.MediaColumns._ID)
                    )

                    val rawDateTaken = stringCol(MediaStore.MediaColumns.DATE_TAKEN).toLong()
                    val datetimeOriginal =
                        DateUtil.dateToExifDateTime(Date(rawDateTaken))

                    val localMedia = LocalMedia(
                        -1,
                        if (isPicture) "Picture" else if (isVideo) "Video" else error("Media must have a type!"),
                        fileName,
                        datetimeOriginal,
                        fileUri.toString(),
                        -1,
                        "",
                        false
                    )

               //     onMediaFile(localMedia)
                    firstPassMediaFiles.add(localMedia) // TODO Remove this once no longer needed for debug
                }
            } else {
                Log.d(TAG, "Skipping media in bucket '$bucketName'.")
            }

            cursor.moveToNext()
        }

        cursor.close()

        i = 0
        firstPassMediaFiles.forEach {

            if (++i % 50 == 0) {
                Log.d(TAG, "Checksum and size progress: ")
            }

            // Calculates size and hash for this file. We need THE ACTUAL size of the file
            // on storage, and it seems that the indexed MediaColumns.SIZE is less than
            // the actual size on disk...
            val fileUri = Uri.parse(it.uri)
            val sizeAndDigest = if (forced) calculateSizeAndMd5ForFile(resolver, fileUri, it.fileName) else 0L to ""
            val fileSize = sizeAndDigest.first
            val digest = sizeAndDigest.second

            it.fileSize = fileSize
            it.checksum = digest
        }

        val duration = System.currentTimeMillis() - startTime

        Log.d(TAG, "Duration: $duration")

    }

    private fun calculateSizeAndMd5ForFile(
        resolver: ContentResolver,
        mediaUri: Uri,
        fileName: String
    ): Pair<Long, String> {
        val digest = MessageDigest.getInstance("MD5")
        val buffer = ByteArray(32768)
        val stream = try {
            // TODO For some reason, on a real device, next line will get stuck after a few
            //  files. See what's up with it...
            resolver.openInputStream(mediaUri)
        } catch (t: Throwable) {
            Log.e(TAG, "Exception opening inputstream for '$fileName': ", t)
            null
        }
        return stream?.let {
            var fileSize = 0L
            var readBytes = stream.read(buffer)
            try {
                while (readBytes > 0) {
                    fileSize += readBytes
                    digest.update(buffer, 0, readBytes)
                    readBytes = stream.read(buffer)
                }
            } catch (t: Throwable) {
                Log.e(TAG, "Failed to calculate MD5 hash for ${fileName}.", t)
            } finally {
                stream.close()
            }

            val md5Sum = digest.digest()
            val bigInt = BigInteger(1, md5Sum)
            val asString = bigInt.toString(16)
            fileSize to String.format("%32s", asString).replace(" ", "0")
        } ?: run {
            throw IllegalStateException("Failed to calculate MD5 hash for ${fileName}.")
        }
    }

    fun getFileLastModifiedDate(context: Context, uriString: String): Date? {
        return DocumentFile.fromSingleUri(context, Uri.parse(uriString))?.let {
            Date(it.lastModified())
        }
    }

}