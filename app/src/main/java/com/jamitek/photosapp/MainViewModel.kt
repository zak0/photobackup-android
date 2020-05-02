package com.jamitek.photosapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.model.Photo

class MainViewModel : ViewModel() {

    /**
     * Offset for next GET to fetch more photos. Used for infinite scrolling/lazy loading.
     */
    private val mutableNextGetOffset = MutableLiveData<Int>().apply { value = 0 }
    val nextGetOffset: LiveData<Int> = mutableNextGetOffset

    /**
     * List of currently loaded photos.
     */
    private val mutablePhotos = MutableLiveData<ArrayList<Photo>>().apply { value = ArrayList() }
    val photos: LiveData<ArrayList<Photo>> = mutablePhotos

    /**
     * Currently selected photo for detailed viewing and inspection.
     */
    private val mutableSelectedPhoto = MutableLiveData<Photo>().apply { value = null }
    val selectedPhoto: LiveData<Photo> = mutableSelectedPhoto

    /**
     * Callback for when a thumbnail is clicked on library screen. Marks the clicked image as
     * selected.
     */
    fun onThumbnailClicked(photo: Photo) {
        mutableSelectedPhoto.value = photo
    }

    /**
     * Callback for when image viewer is opened.
     * TODO Trigger necessary following actions. (GET for meta data, ...)
     */
    fun onImageViewerOpened() {
        mutableSelectedPhoto.value = null
    }

    /**
     * Callback for library scroll listener indicating that user is approaching the end of
     * currently loaded photos and more should be loaded.
     */
    fun onShouldLoadMorePhotos() {
        mutableNextGetOffset.value = (photos.value?.size ?: -1) + 1
    }

    /**
     * Callback for when a GET for photos finishes.
     *
     * TODO When infinite scrolling is built, add photos to the list, but only add new ones
     *  to avoid duplicates. There should not be duplicates to begin with, so keep an eye on
     *  this when check for duplicates is built.
     */
    fun onPhotosLoaded(newPhotos: List<Photo>) {
        if (newPhotos.size != mutablePhotos.value?.size ?: 0) {
            mutablePhotos.value = ArrayList(newPhotos)
        } else {
            mutablePhotos.value = mutablePhotos.value
        }
    }

}
