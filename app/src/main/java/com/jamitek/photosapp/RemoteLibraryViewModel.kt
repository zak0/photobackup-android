package com.jamitek.photosapp

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jamitek.photosapp.model.Photo
import kotlinx.coroutines.launch

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
    private val mutableSelectedPhoto = MutableLiveData<Photo>().apply { value = null }
    val selectedPhoto: LiveData<Photo> = mutableSelectedPhoto

    val photosPerDate = repository.photosPerDate

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

    fun refreshRemotePhotos() {
        repository.fetchRemotePhotos()
    }


}
