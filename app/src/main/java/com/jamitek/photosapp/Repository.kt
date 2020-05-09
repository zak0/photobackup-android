package com.jamitek.photosapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.database.MetaDataDb
import com.jamitek.photosapp.database.SqliteMetaDataDb
import com.jamitek.photosapp.model.Photo
import kotlinx.coroutines.*

object Repository {

    private const val TAG = "Repository"

    private lateinit var metaDataDb: MetaDataDb

    /**
     * List of currently loaded photos.
     */
    private val mutableAllPhotos = MutableLiveData<ArrayList<Photo>>().apply { value = ArrayList() }
    val allPhotos: LiveData<ArrayList<Photo>> = mutableAllPhotos

    /**
     * Photos for "timeline" neatly organized into per-date buckets.
     */
    private val mutablePhotosPerDate =
        MutableLiveData<ArrayList<Pair<String, ArrayList<Photo>>>>().apply { value = ArrayList() }
    val photosPerDate: LiveData<ArrayList<Pair<String, ArrayList<Photo>>>> = mutablePhotosPerDate

    /**
     * Task that loads and processes metadata already persisted in the database. This is only
     * necessary during a cold start of the app.
     */
    var processPersistedPhotosMetaTask: Deferred<Unit>? = null

    /**
     * Task that processes and persists received metadata from the server.
     */
    var processRemotePhotosTask: Deferred<Unit>? = null

    /**
     * Task that processes and persists metadata for photos in local mass storage.
     */
    var processLocalPhotosTask: Deferred<Unit>? = null

    fun init(context: Context) {
        metaDataDb = SqliteMetaDataDb(context)
        GlobalScope.launch { initPhotosFromDatabase() }
    }

    /**
     * Initializes the metadata for all photos from the database. This should be done when the
     * repository gets initialized.
     *
     * TODO: Consider at this time also checking for files that no longer exist locally.
     */
    private suspend fun initPhotosFromDatabase() {
        coroutineScope {
            processPersistedPhotosMetaTask = async(Dispatchers.Default) {
                val metaFromDb = metaDataDb.getAllPhotos()
                withContext(Dispatchers.Main) {
                    mutableAllPhotos.value = metaFromDb
                    arrangeIntoDateBuckets()
                }
            }
        }
    }

    /**
     * Callback for when network request to fetch remote photos finishes.
     */
    suspend fun onRemotePhotosLoaded(remotePhotos: List<Photo>) {
        coroutineScope {
            // Wait for processing of local files and DB initialization to finish before continuing.
            processPersistedPhotosMetaTask?.await()
            processLocalPhotosTask?.await()

            processRemotePhotosTask = async(Dispatchers.Default) {
                // We're now only interested in Photo's that don't already have a remote photo
                // linked to them and
                mutableAllPhotos.value?.also { photos ->
                    remotePhotos.forEach { remotePhoto ->
                        // Check for already existing ones (by fileSize and hash) and do nothing if found.
                        if (photos.count {
                                photosAreEqual(remotePhoto, it)
                            } > 0) {
                            Log.d(
                                TAG,
                                "Remote photo '${remotePhoto.fileName}' already exists. Skipping."
                            )
                            return@forEach
                        }

                        // Map to potentially existing LocalPhotos (by fileSize and hash)
                        photos
                            .filter { it.serverId == null && it.localUriString != null }
                            .find { existingLocalPhoto ->
                                photosAreEqual(remotePhoto, existingLocalPhoto)
                            }
                            ?.also { photoWithMatchingLocal ->
                                // We're here if there was a local photo, that didn't yet have a
                                // matching remote photo attached to it.
                                // Update the record with data of the remote photo.
                                photoWithMatchingLocal.serverId = remotePhoto.serverId
                                photoWithMatchingLocal.serverDirPath = remotePhoto.serverDirPath
                                Log.d(
                                    TAG,
                                    "Remote photo '${remotePhoto.fileName}' existed locally. Linking."
                                )
                                metaDataDb.persistPhoto(photoWithMatchingLocal)
                                return@forEach
                            }

                        // We're here if the remote photo didn't already exist, nor was there a
                        // matching already existing local photo where it was mapped to.
                        // This is a new photo, so let's create a new Photo object with just
                        // data for the remote photo.
                        Log.d(TAG, "Remote photo '${remotePhoto.fileName}' is new one. Adding.")
                        val newPhoto = Photo(
                            null,
                            remotePhoto.serverId,
                            remotePhoto.fileName,
                            remotePhoto.fileSize,
                            remotePhoto.serverDirPath,
                            null,
                            null,
                            remotePhoto.hash,
                            remotePhoto.dateTimeOriginal,
                            remotePhoto.status
                        )
                        photos.add(newPhoto)
                        metaDataDb.persistPhoto(newPhoto)
                    }
                }

                // Done. Notify observers.
                withContext(Dispatchers.Main) {
                    mutableAllPhotos.value = mutableAllPhotos.value
                }

                // TODO Do this only if local photos processing is not ongoing.
                //  in which case this would now be done twice.
                arrangeIntoDateBuckets()
            }
        }

    }

    /**
     * Callback for when camera directory has been scanned for local photos.
     */
    suspend fun onLocalPhotosLoaded(localPhotos: List<Photo>) {
        // TODO Check for already existing ones (by fileSize and hash) and do nothing if found.
        // TODO Map to potentially existing RemotePhotos (by fileSize and hash)


        coroutineScope {
            // Wait for processing of remote files to finish before continuing.
            processRemotePhotosTask?.await()
            processLocalPhotosTask = async(Dispatchers.Default) {
                // YOLO
            }
        }
    }

    private fun photosAreEqual(
        leftPhoto: Photo,
        rightPhoto: Photo
    ): Boolean {
        return leftPhoto.fileSize == rightPhoto.fileSize && leftPhoto.hash == rightPhoto.hash
    }

    private suspend fun arrangeIntoDateBuckets() {
        val newPhotosPerDate = ArrayList<Pair<String, ArrayList<Photo>>>()

        var currentDate = ""
        var photosForCurrentDate = ArrayList<Photo>()

        mutableAllPhotos.value?.forEach { photo ->
            val date = DateUtil.exifDateToNiceDate(photo.dateTimeOriginal)

            // If date differs from the date currently being processed, then add these photos to the
            // list and init the list for the next date.
            if (currentDate != date) {
                // Eliminate adding the first one by only adding the pair if there are photos for
                // this date. (Which should be the case for all other than the initialized first
                // date (empty string).
                if (photosForCurrentDate.size > 0) {
                    val pair = Pair(currentDate, photosForCurrentDate)
                    newPhotosPerDate.add(pair)
                }
                currentDate = date
                photosForCurrentDate = ArrayList()
            }

            photosForCurrentDate.add(photo)
        }

        // Add the last pair
        val pair = Pair(currentDate, photosForCurrentDate)
        newPhotosPerDate.add(pair)

        withContext(Dispatchers.Main) { mutablePhotosPerDate.value = newPhotosPerDate }
    }
}
