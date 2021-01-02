package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.jamitek.photosapp.model.DisplayableMedia
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.remotelibrary.MediaTimelineUseCase

class MediaTimelineViewModel(
    private val timelineUseCase: MediaTimelineUseCase,
) : ViewModel() {

    val urlIsSet
        get() = timelineUseCase.urlIsSet

    val allMedia = timelineUseCase.allMedia
    val groupedMedia = timelineUseCase.groupedMedia
    val lastBackupTimestamp = timelineUseCase.lastBackupTimestamp

    private val mutableSelectedPhoto = MutableLiveData<RemoteMedia>(null)
    val selectedRemoteMedia: LiveData<RemoteMedia> = mutableSelectedPhoto

    fun onThumbnailClicked(remoteMedia: RemoteMedia) {
        mutableSelectedPhoto.value = remoteMedia
    }

    fun onImageViewerOpened() {
        mutableSelectedPhoto.value = null
    }

    fun refreshRemotePhotos() {
        timelineUseCase.refreshRemotePhotos()
    }

    fun authorizedThumbnailGlideUrl(mediIdOnServer: Int): GlideUrl {
        val thumbUrl = timelineUseCase.thumbnailUrl(mediIdOnServer)
        return authorizedGlideUrl(thumbUrl)
    }

    fun authorizedImageGlideUrl(mediaIdOnServer: Int): GlideUrl {
        val imageUrl = timelineUseCase.mediaUrl(mediaIdOnServer)
        return authorizedGlideUrl(imageUrl)
    }

    private fun authorizedGlideUrl(url: String): GlideUrl = GlideUrl(
        url,
        timelineUseCase.authHeader?.let { authHeader ->
            LazyHeaders.Builder()
                .addHeader(authHeader.first, authHeader.second)
                .build()
        }
    )

}
