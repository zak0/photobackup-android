package com.jamitek.photosapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val mutableSelectedPhoto = MutableLiveData<Int>().apply { value = null }
    val selectedPhoto: LiveData<Int> = mutableSelectedPhoto

    fun onThumbnailClicked(photoId: Int) {
        mutableSelectedPhoto.value = photoId
    }

    fun onImageViewerOpened() {
        mutableSelectedPhoto.value = null
    }

}
