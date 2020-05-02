package com.jamitek.photosapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jamitek.photosapp.model.Photo

class MainViewModel : ViewModel() {
    private val mutableSelectedPhoto = MutableLiveData<Photo>().apply { value = null }
    val selectedPhoto: LiveData<Photo> = mutableSelectedPhoto

    fun onThumbnailClicked(photo: Photo) {
        mutableSelectedPhoto.value = photo
    }

    fun onImageViewerOpened() {
        mutableSelectedPhoto.value = null
    }

}
