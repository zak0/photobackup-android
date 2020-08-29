package com.jamitek.photosapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.model.Photo
import com.jamitek.photosapp.networking.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteLibraryRepository(private val libraryApi: ApiClient) {

    companion object {
        private const val TAG = "RemoteLibraryRepository"
    }

    private val mutableAllPhotos = MutableLiveData<List<Photo>>().apply { value = emptyList() }
    val allPhotos: LiveData<List<Photo>> = mutableAllPhotos

    /**
     * Photos for "timeline" neatly organized into per-date buckets.
     */
    private val mutablePhotosPerDate =
        MutableLiveData<ArrayList<Pair<String, ArrayList<Photo>>>>().apply { value = ArrayList() }
    val photosPerDate: LiveData<ArrayList<Pair<String, ArrayList<Photo>>>> = mutablePhotosPerDate

    fun fetchRemotePhotos() {
        libraryApi.getAllPhotos { success, photos ->
            GlobalScope.launch {
                arrangeIntoDateBuckets(photos)
            }
        }
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
    private suspend fun arrangeIntoDateBuckets(photos: List<Photo>) {
        val newPhotosPerDate = ArrayList<Pair<String, ArrayList<Photo>>>()

        var currentDate = ""
        var photosForCurrentDate = ArrayList<Photo>()

        photos.sortedByDescending { it.dateTimeOriginal }.forEach { photo ->
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

        // Put the photos in the same order into the "all photos" array. This array is used for
        // swiping through photos.
        val newAllPhotos = ArrayList<Photo>()
        newPhotosPerDate.forEach { datePhotosPair ->
            datePhotosPair.second.forEach { photo ->
                newAllPhotos.add(photo)
            }
        }

        Log.d(TAG, "Arrange by date - Done. Notifying observers...")
        withContext(Dispatchers.Main) {
            mutablePhotosPerDate.value = newPhotosPerDate
            mutableAllPhotos.value = newAllPhotos
        }
    }
}
