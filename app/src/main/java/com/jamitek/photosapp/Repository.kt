package com.jamitek.photosapp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.database.MetaDataDb
import com.jamitek.photosapp.database.SqliteMetaDataDb
import com.jamitek.photosapp.model.Photo
import com.jamitek.photosapp.storage.ExifHelper
import com.jamitek.photosapp.storage.StorageAccessHelper
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
        val startTime = System.currentTimeMillis()
        coroutineScope {
            processPersistedPhotosMetaTask = async(Dispatchers.Default) {
                val metaFromDb = metaDataDb.getAllPhotos()
                withContext(Dispatchers.Main) {
                    mutableAllPhotos.value = metaFromDb
                    arrangeIntoDateBuckets()
                }
                val duration = System.currentTimeMillis() - startTime
                Log.d(TAG, "initPhotosFromDatabase() - complete in $duration ms")
                Unit
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
                mutableAllPhotos.value?.also { photos ->
                    remotePhotos.forEach { remotePhoto ->
                        // Check if a record for this photo already exists in meta data DB.
                        // If it does, then update the existing local file record with remote
                        // information.
                        photos.firstOrNull { existingPhoto ->
                            photosAreEqual(existingPhoto, remotePhoto)
                        }?.also { existingPhoto ->
                            // If remote info differs, update and persist.
                            if (existingPhoto.serverId != remotePhoto.serverId
                                || existingPhoto.serverDirPath != remotePhoto.serverDirPath
                                || existingPhoto.status != remotePhoto.status
                            ) {
                                existingPhoto.serverId = remotePhoto.serverId
                                existingPhoto.serverDirPath = remotePhoto.serverDirPath
                                existingPhoto.status = remotePhoto.status
                                Log.d(
                                    TAG,
                                    "Remote - Updating existing record for `${remotePhoto.fileName}`."
                                )
                                metaDataDb.persistPhoto(existingPhoto)
                            } else {
                                Log.d(TAG, "Remote - Photo `${remotePhoto.fileName}` is already up to date.")
                            }
                        } ?: run {
                            // Insert a new photo, if it didn't already exist.
                            Log.d(TAG, "Remote - '${remotePhoto.fileName}' is new one. Adding.")
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
                }

                // Sort the photos by date
                mutableAllPhotos.value?.sortByDescending { it.dateTimeOriginal }

                // Done. Notify observers.
                withContext(Dispatchers.Main) {
                    mutableAllPhotos.value = mutableAllPhotos.value
                }

                // TODO Do this only if local photos processing is not ongoing.
                //  in which case this would now be done multiple times.
                arrangeIntoDateBuckets()
            }
        }

    }

    /**
     * Callback for when camera directory has been scanned for local photos.
     */
    suspend fun onLocalPhotosLoaded(context: Context, localPhotos: List<Photo>) {
        coroutineScope {
            // Wait for processing of remote files to finish before continuing.
            processPersistedPhotosMetaTask?.await()
            processRemotePhotosTask?.await()

            processLocalPhotosTask = async(Dispatchers.Default) {
                mutableAllPhotos.value?.also { photos ->
                    localPhotos.forEach { localPhoto ->
                        // Check if a record for this photo already exists in meta data DB.
                        // If it does, then update the existing local file record with remote
                        // information.
                        photos.firstOrNull { existingPhoto ->
                            photosAreEqual(existingPhoto, localPhoto)
                        }?.also { existingPhoto ->
                            if (existingPhoto.fileName != localPhoto.fileName
                                || existingPhoto.localUriString != localPhoto.localUriString
                                || existingPhoto.localThumbnailUriString != localPhoto.localThumbnailUriString
                            ) {
                                // NOTE!! Datetimeoriginal is disregarded in this comparison, and
                                // setting, because it is currently only calculated when inserting
                                // the photo into the meta data db for the first time!
                                // I.e. the localPhoto above does not have datetimeoriginal in it.
                                existingPhoto.fileName = localPhoto.fileName
                                existingPhoto.localUriString = localPhoto.localUriString
                                existingPhoto.localThumbnailUriString =
                                    localPhoto.localThumbnailUriString

                                Log.d(TAG, "Local - Updating existing record for '${localPhoto.fileName}'.")
                                metaDataDb.persistPhoto(existingPhoto)
                            } else {
                                Log.d(TAG, "Local - Existing record for '${localPhoto.fileName}' was already up to date.")
                            }
                        } ?: run {
                            localPhoto.localUriString?.also { localUriString ->
                                // Attempt to read EXIF time, fall back to file modification date, and
                                // finally fall back to epoch time.
                                val dateTimeOriginal =
                                    ExifHelper.getDateTimeOriginal(context, localUriString)
                                        ?: StorageAccessHelper.getFileLastModifiedDate(
                                            context,
                                            localUriString
                                        )?.let { DateUtil.dateToExifDate(it) } ?: let {
                                            Log.e(
                                                TAG,
                                                "Local - Unable to get timestamp for '${localPhoto.fileName}'"
                                            )
                                            DateUtil.EPOCH_EXIF
                                        }

                                val newPhoto = Photo(
                                    null,
                                    null,
                                    localPhoto.fileName,
                                    localPhoto.fileSize,
                                    null,
                                    localUriString,
                                    localUriString,
                                    localPhoto.hash,
                                    dateTimeOriginal,
                                    localPhoto.status
                                )
                                photos.add(newPhoto)

                                Log.d(TAG, "Local - Inserting a new record for '${localPhoto.fileName}'.")
                                metaDataDb.persistPhoto(newPhoto)
                            } ?: run {
                                Log.e(
                                    TAG,
                                    "Local - '${localPhoto.fileName}' is new one, but didn't have an URI!?"
                                )
                            }
                        }
                    }

                    // Sort the photos by date
                    photos.sortByDescending { it.dateTimeOriginal }
                }

                // Notify observers
                withContext(Dispatchers.Main) { mutableAllPhotos.value = mutableAllPhotos.value }

                // TODO Do this only if local photos processing is not ongoing.
                //  in which case this would now be done multiple times.
                arrangeIntoDateBuckets()
            }
        }
    }

    private fun photosAreEqual(
        leftPhoto: Photo,
        rightPhoto: Photo
    ): Boolean {
        return leftPhoto.fileSize == rightPhoto.fileSize && leftPhoto.hash == rightPhoto.hash
    }

    /**
     * Arranges photos into "buckets" based on their date. This allows for nice grouping of photos
     * and displaying then on a timeline.
     *
     * Only local photos and remote photos on state [Photo.Status.READY] are included in the
     * buckets.
     *
     * The resulting grouping is stored in observable [photosPerDate] [ArrayList].
     */
    private suspend fun arrangeIntoDateBuckets() {
        val newPhotosPerDate = ArrayList<Pair<String, ArrayList<Photo>>>()

        var currentDate = ""
        var photosForCurrentDate = ArrayList<Photo>()

        mutableAllPhotos.value?.sortedByDescending { it.dateTimeOriginal }?.forEach { photo ->
            // Skip if this is a remote-only photo, that is not on state READY.
            if (!photo.isLocal && photo.status != Photo.Status.READY) {
                Log.d(TAG, "Arrange by date - skipping unready ${photo.fileName}")
                return@forEach
            }

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

        newPhotosPerDate.sortByDescending { it.second.firstOrNull()?.dateTimeOriginal }

        Log.d(TAG, "Arrange by date - Done. Notifying observers...")
        withContext(Dispatchers.Main) { mutablePhotosPerDate.value = newPhotosPerDate }
    }
}
