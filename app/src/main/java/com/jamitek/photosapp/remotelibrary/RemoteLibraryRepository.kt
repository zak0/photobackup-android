package com.jamitek.photosapp.remotelibrary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.api.ApiClient
import com.jamitek.photosapp.util.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteLibraryRepository(private val libraryApi: ApiClient) {

    companion object {
        private const val TAG = "RemoteLibraryRepository"
    }

    private val mutableAllPhotos =
        MutableLiveData<List<RemoteMedia>>().apply { value = emptyList() }
    val allMedia: LiveData<List<RemoteMedia>> = mutableAllPhotos

    /**
     * Media for "timeline" neatly organized into per-date buckets.
     */
    private val mutableMediaPerDate =
        MutableLiveData<ArrayList<Pair<String, ArrayList<RemoteMedia>>>>().apply {
            value = ArrayList()
        }
    val mediaPerDate: LiveData<ArrayList<Pair<String, ArrayList<RemoteMedia>>>> =
        mutableMediaPerDate

    /**
     * Media in monthly buckets
     */
    private val mutableMediaPerMonth =
        MutableLiveData<ArrayList<Pair<String, ArrayList<RemoteMedia>>>>().apply {
            value = ArrayList()
        }
    val mediaPerMonth: LiveData<ArrayList<Pair<String, ArrayList<RemoteMedia>>>> =
        mutableMediaPerMonth

    fun fetchRemotePhotos() {
        libraryApi.getAllMedia { success, photos ->
            GlobalScope.launch {
                arrangeIntoDateBuckets(photos)
                arrangeIntoMonthBuckets(photos)
            }
        }
    }

    /**
     * Arranges media into "buckets" based on their date. This allows for nice grouping of media
     * and displaying then on a timeline.
     *
     * Only remote media on state [RemoteMedia.Status.READY] is included in the
     * buckets.
     *
     * The resulting grouping is stored in observable [mediaPerDate] [ArrayList].
     */
    private suspend fun arrangeIntoDateBuckets(remoteMedia: List<RemoteMedia>) {
        val newPhotosPerDate = ArrayList<Pair<String, ArrayList<RemoteMedia>>>()

        var currentDate = ""
        var photosForCurrentDate = ArrayList<RemoteMedia>()

        remoteMedia.sortedByDescending { it.dateTimeOriginal }.forEach { photo ->
            // Skip if this is not on state READY.
            if (photo.status != RemoteMedia.Status.READY) {
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
        val newAllPhotos = ArrayList<RemoteMedia>()
        newPhotosPerDate.forEach { datePhotosPair ->
            datePhotosPair.second.forEach { photo ->
                newAllPhotos.add(photo)
            }
        }

        Log.d(TAG, "Arrange by date - Done. Notifying observers...")
        withContext(Dispatchers.Main) {
            mutableMediaPerDate.value = newPhotosPerDate
            mutableAllPhotos.value = newAllPhotos
        }
    }

    /**
     * Arranges media into "buckets" based on the month they were created. This allows for nice
     * grouping of media and displaying then on a timeline.
     *
     * Ony remote media on state [RemoteMedia.Status.READY] are included in the
     * buckets.
     *
     * The resulting grouping is stored in observable [mediaPerMonth] [ArrayList].
     */
    private suspend fun arrangeIntoMonthBuckets(remoteMedia: List<RemoteMedia>) {
        val newPhotosPerMonth = ArrayList<Pair<String, ArrayList<RemoteMedia>>>()

        var currentMonth = ""
        var photosForCurrentMonth = ArrayList<RemoteMedia>()

        remoteMedia.sortedByDescending { it.dateTimeOriginal }.forEach { photo ->
            // Skip if this is not on state READY.
            if (photo.status != RemoteMedia.Status.READY) {
                Log.d(TAG, "Arrange by date - skipping unready ${photo.fileName}")
                return@forEach
            }

            val month = DateUtil.exifDateToNiceMonthAndYear(photo.dateTimeOriginal)

            if (currentMonth != month) {
                if (photosForCurrentMonth.size > 0) {
                    val pair = Pair(currentMonth, photosForCurrentMonth)
                    newPhotosPerMonth.add(pair)
                }
                currentMonth = month
                photosForCurrentMonth = ArrayList()
            }

            photosForCurrentMonth.add(photo)

        }

        // Add the last pair
        val pair = Pair(currentMonth, photosForCurrentMonth)
        newPhotosPerMonth.add(pair)

        newPhotosPerMonth.sortByDescending { it.second.firstOrNull()?.dateTimeOriginal }

        Log.d(TAG, "Arrange by month - Done. Notifying observers...")
        withContext(Dispatchers.Main) {
            mutableMediaPerMonth.value = newPhotosPerMonth
        }
    }
}
