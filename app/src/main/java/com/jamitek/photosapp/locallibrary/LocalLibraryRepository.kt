package com.jamitek.photosapp.locallibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.KeyValueStore.Companion.KEY_CAMERA_DIR_URI
import com.jamitek.photosapp.database.LocalMedia
import com.jamitek.photosapp.database.LocalMediaDb
import kotlinx.coroutines.*

class LocalLibraryRepository(
    private val keyValueStore: KeyValueStore,
    private val db: LocalMediaDb,
    private val scanner: LocalLibraryScanner
) {

    private var initJob: Job? = null
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

    fun scan() {
        // Only allow starting the scan if initialization is complete.
        // Scan relies on being able to check if given file already is known, and whether it's
        // already backed up.
        if (initJob?.isActive == true) {
            return
        }

        requireNotNull(mutableStatus.value) { "Status should have been initialized by now" }

        cameraDirUriString?.also { uriString ->
            updateStatus(true)
            CoroutineScope(Dispatchers.IO).launch {
                scanner.iterateCameraDir(uriString) { localMedia ->
                    CoroutineScope(Dispatchers.IO).launch {
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

                    // Update
                    updateStatus(false)
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
