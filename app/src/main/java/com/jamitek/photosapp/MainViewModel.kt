package com.jamitek.photosapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jamitek.photosapp.model.Photo
import com.jamitek.photosapp.model.RemotePhoto
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    /**
     * Offset for next GET to fetch more photos. Used for infinite scrolling/lazy loading.
     */
    private val mutableNextGetOffset = MutableLiveData<Int>().apply { value = 0 }
    val nextGetOffset: LiveData<Int> = mutableNextGetOffset

    /**
     * List of currently loaded photos.
     */
    val photos = Repository.allPhotos

    /**
     * Currently selected photo for detailed viewing and inspection.
     */
    private val mutableSelectedPhoto = MutableLiveData<Photo>().apply { value = null }
    val selectedPhoto: LiveData<Photo> = mutableSelectedPhoto

    val photosPerDate = Repository.photosPerDate

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
     */
    fun onRemotePhotosLoaded(newPhotos: List<RemotePhoto>) {
        viewModelScope.launch { Repository.onRemotePhotosLoaded(newPhotos) }
    }



}
