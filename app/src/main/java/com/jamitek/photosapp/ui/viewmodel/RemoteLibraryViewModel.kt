package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.remotelibrary.RemoteLibraryRepository

class RemoteLibraryViewModel(
    private val repository: RemoteLibraryRepository
) : ViewModel() {

    /**
     * List of currently loaded photos.
     */
    val photos = repository.allPhotos

    /**
     * Currently selected photo for detailed viewing and inspection.
     */
    private val mutableSelectedPhoto = MutableLiveData<RemoteMedia>().apply { value = null }
    val selectedRemoteMedia: LiveData<RemoteMedia> = mutableSelectedPhoto

    val photosPerDate = repository.photosPerDate

    /**
     * Callback for when a thumbnail is clicked on library screen. Marks the clicked image as
     * selected.
     */
    fun onThumbnailClicked(remoteMedia: RemoteMedia) {
        mutableSelectedPhoto.value = remoteMedia
    }

    /**
     * Callback for when image viewer is opened.
     * TODO Trigger necessary following actions. (GET for meta data, ...)
     */
    fun onImageViewerOpened() {
        mutableSelectedPhoto.value = null
    }

    fun refreshRemotePhotos() {
        repository.fetchRemotePhotos()
    }


}
