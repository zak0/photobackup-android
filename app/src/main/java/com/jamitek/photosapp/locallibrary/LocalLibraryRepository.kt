package com.jamitek.photosapp.locallibrary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.KeyValueStore.Companion.KEY_CAMERA_DIR_URI
import com.jamitek.photosapp.database.LocalMediaDb
import com.jamitek.photosapp.model.LocalMedia
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.networking.ApiClient
import com.jamitek.photosapp.storage.StorageAccessHelper
import kotlinx.coroutines.*

class LocalLibraryRepository(
    private val keyValueStore: KeyValueStore,
    private val db: LocalMediaDb,
    private val scanner: LocalLibraryScanner,
    private val storageHelper: StorageAccessHelper,
    private val api: ApiClient
) {

    companion object {
        private const val TAG = "LocalLibRepo"
    }

    private var initJob: Job? = null
    private var scanJob: Job? = null
    private val cache = HashSet<LocalMedia>()
    private val cacheByCheckSum = HashMap<String, LocalMedia>()

    init {
        initJob = GlobalScope.launch {
            cache.clear()
            cache.addAll(db.getAll())

            cacheByCheckSum.clear()
            cacheByCheckSum.putAll(cache.map { it.checksum to it })

            updateStatus(false)
        }
    }

    private val mutableStatus = MutableLiveData<LocalLibraryStatus?>().apply { value = null }
    val status: LiveData<LocalLibraryStatus?> = mutableStatus

    var cameraDirUriString: String?
        get() = keyValueStore.getString(KEY_CAMERA_DIR_URI)
        set(value) {
            keyValueStore.putString(KEY_CAMERA_DIR_URI, value)
        }

    /**
     * Scans camera directory for media files and maintains an index of them in a database.
     */
    fun scan() {
        // Only allow starting the scan if initialization is complete.
        // Scan relies on being able to check if given file already is known, and whether it's
        // already backed up.
        if (initJob?.isActive == true) {
            return
        }

        // Only allow one scan to be running at any given time
        if (scanJob?.isActive == true) {
            Log.d(TAG, "Scan already running. Not starting a new one...")
            return
        }

        requireNotNull(mutableStatus.value) { "Status should have been initialized by now" }

        cameraDirUriString?.also { uriString ->
            updateStatus(true)
            scanJob = CoroutineScope(Dispatchers.IO).launch {
                scanner.iterateCameraDir(uriString) { localMedia ->
                    cacheByCheckSum[localMedia.checksum]?.also { existingMedia ->
                        // We're here if this file was already known. It could be that the URI
                        // has changed, so let's check for that, and update the DB if that's the
                        // case.
                        if (cacheByCheckSum[localMedia.checksum]!!.uri != localMedia.uri) {
                            // Update the uri of existing media, because it contains the DB ID.
                            // This way the existing DB entry is updated.
                            existingMedia.checksum = localMedia.checksum
                            db.persist(existingMedia)
                        }
                    } ?: run {
                        // We're here if this file is so-far unknown. It didn't exist in the database.
                        db.persist(localMedia)
                        cacheLocalMedia(localMedia)
                    }
                }

                // Update library status once scan is complete
                updateStatus(false)
            }
        }
    }

    /**
     * Backs up local media files to the server. Only backs up files that do not yet exist on the
     * server (that the app knows of).
     *
     * If an API request times out, server is assumed to be out of reach and backup is stopped.
     *
     * Backup will not be initiated if a scan is in progress.
     */
    fun backup() {
        if (scanJob?.isActive == true) {
            Log.d(TAG, "Backup cannot be started while scan is in progress...")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            // Get all files that don't appear to be backed up yet
            cache.filter { !it.uploaded }.forEach { localMedia ->
                // First POST meta data. If the POST succeeds, the API responds with ID of the
                // meta data on the server
                val metaPostResponse = api.postPhotoMetaData(localMedia)

                if (metaPostResponse.statusCode in 200..201) {
                    // Status code is 200 if the server already knew of this file.
                    // Status code is 201 if this was a new file that the server didn't have before.
                    if (metaPostResponse.data?.status == RemoteMedia.Status.UPLOAD_PENDING) {
                        storageHelper.getFileAsByteArray(localMedia.uri)?.also { bytes ->
                            val success = api.uploadPhoto(
                                metaPostResponse.data.serverId,
                                localMedia,
                                bytes
                            ).data == true
                            Log.d(
                                TAG,
                                "Media upload ${if (success) "success" else "failed"} for `${localMedia.fileName}`"
                            )

                            // If upload was a success, let's mark the file as uploaded into the DB
                            if (success) {
                                localMedia.uploaded = true
                                db.persist(localMedia)
                            }
                        }
                    } else if (metaPostResponse.data?.status == RemoteMedia.Status.READY) {
                        // Photo is already uploaded. Let's mark it as such in our DB
                        localMedia.uploaded = true
                        db.persist(localMedia)
                        Log.d(TAG, "Media '${localMedia.fileName}' already existed on the server")
                    }
                }
            }
        }
    }

    private fun cacheLocalMedia(localMedia: LocalMedia) {
        cache.add(localMedia)
        cacheByCheckSum[localMedia.checksum] = localMedia
    }

    private fun updateStatus(isScanning: Boolean) {
        CoroutineScope(Dispatchers.Main).launch {
            mutableStatus.value =
                LocalLibraryStatus(isScanning, cache.size, cache.count { !it.uploaded })
        }
    }

}
