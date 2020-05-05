package com.jamitek.photosapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.model.LocalPhoto
import com.jamitek.photosapp.model.Photo
import com.jamitek.photosapp.model.RemotePhoto
import kotlinx.coroutines.*

object Repository {

    private const val TAG = "Repository"

    /**
     * List of currently loaded photos.
     */
    private val mutableAllPhotos = MutableLiveData<ArrayList<Photo>>().apply { value = ArrayList() }
    val allPhotos: LiveData<ArrayList<Photo>> = mutableAllPhotos

    /**
     * Photos for "timeline" neatly organized into per-date buckets.
     */
    private val mutablePhotosPerDate = MutableLiveData<ArrayList<Pair<String, ArrayList<Photo>>>>().apply { value = ArrayList() }
    val photosPerDate: LiveData<ArrayList<Pair<String, ArrayList<Photo>>>> = mutablePhotosPerDate

    var processRemotePhotosTask: Deferred<Unit>? = null
    var processLocalPhotosTask: Deferred<Unit>? = null

    /**
     * Callback for when network request to fetch remote photos finishes.
     */
    suspend fun onRemotePhotosLoaded(remotePhotos: List<RemotePhoto>) {
        coroutineScope {
            // Wait for processing of local files to finish before continuing.
            processLocalPhotosTask?.await()

            processRemotePhotosTask = async(Dispatchers.Default) {
                // We're now only interested in Photo's that don't already have a remote photo
                // linked to them and
                mutableAllPhotos.value?.also { photos ->
                    remotePhotos.forEach { remotePhoto ->
                        // Check for already existing ones (by fileSize and hash) and do nothing if found.
                        if (photos.count {
                                it.remotePhoto?.let { existingRemotePhoto ->
                                    photosAreEqual(remotePhoto, existingRemotePhoto)
                                } == true
                            } > 0) {
                            Log.d(
                                TAG,
                                "Remote photo '${remotePhoto.fileName}' already exists. Skipping."
                            )
                            return@forEach
                        }

                        // Map to potentially existing LocalPhotos (by fileSize and hash)
                        photos
                            .filter { it.remotePhoto == null && it.localPhoto != null }
                            .find {
                                it.localPhoto?.let { existingLocalPhoto ->
                                    photosAreEqual(remotePhoto, existingLocalPhoto)
                                } == true
                            }
                            ?.also { photoWithMatchingLocal ->
                                photoWithMatchingLocal.remotePhoto = remotePhoto
                                Log.d(TAG, "Remote photo '${remotePhoto.fileName}' existed locally. Linking.")
                                return@forEach
                            }

                        // We're here if the remote photo didn't already exist, nor was there a
                        // matching already existing local photo where it was mapped to.
                        // This is a new photo, so let's create a new Photo object with just
                        // its remotePhoto pointing to this new remote photo.
                        Log.d(TAG, "Remote photo '${remotePhoto.fileName}' is new one. Adding.")
                        photos.add(Photo(remotePhoto = remotePhoto))
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
    suspend fun onLocalPhotosLoaded(localPhotos: List<LocalPhoto>) {
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
        leftRemotePhoto: RemotePhoto,
        rightRemotePhoto: RemotePhoto
    ): Boolean {
        return leftRemotePhoto.fileSize == rightRemotePhoto.fileSize && leftRemotePhoto.hash == rightRemotePhoto.hash
    }

    private fun photosAreEqual(remote: RemotePhoto, local: LocalPhoto): Boolean {
        return remote.fileSize == local.fileSize && remote.hash == local.hash
    }

    private suspend fun arrangeIntoDateBuckets() {
        val newPhotosPerDate = ArrayList<Pair<String, ArrayList<Photo>>>()

        var currentDate = ""
        var photosForCurrentDate = ArrayList<Photo>()

        mutableAllPhotos.value?.forEach { photo ->
            // TODO Also handle photo.localPhoto. Now only handler .remotePhoto.
            photo.remotePhoto?.also { remotePhoto ->
                val date = DateUtil.exifDateToNiceDate(remotePhoto.dateTimeOriginal)

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
        }

        // Add the last pair
        val pair = Pair(currentDate, photosForCurrentDate)
        newPhotosPerDate.add(pair)

        withContext(Dispatchers.Main) { mutablePhotosPerDate.value = newPhotosPerDate }
    }
}
