package com.jamitek.photosapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.remotelibrary.RemoteLibraryBrowserUseCase

class RemoteLibraryViewModel(
    private val remoteLibraryUseCase: RemoteLibraryBrowserUseCase
) : ViewModel() {

    val urlIsSet
        get() = remoteLibraryUseCase.urlIsSet

    val allMedia = remoteLibraryUseCase.allMedia
    val groupedMedia = remoteLibraryUseCase.groupedMedia

    private val mutableSelectedPhoto = MutableLiveData<RemoteMedia>(null)
    val selectedRemoteMedia: LiveData<RemoteMedia> = mutableSelectedPhoto

    fun onThumbnailClicked(remoteMedia: RemoteMedia) {
        mutableSelectedPhoto.value = remoteMedia
    }

    fun onImageViewerOpened() {
        mutableSelectedPhoto.value = null
    }

    fun refreshRemotePhotos() {
        remoteLibraryUseCase.refreshRemotePhotos()
    }

    fun authorizedThumbnailGlideUrl(mediIdOnServer: Int): GlideUrl {
        val thumbUrl = remoteLibraryUseCase.thumbnailUrl(mediIdOnServer)
        return authorizedGlideUrl(thumbUrl)
    }

    fun authorizedImageGlideUrl(mediaIdOnServer: Int): GlideUrl {
        val imageUrl = remoteLibraryUseCase.mediaUrl(mediaIdOnServer)
        return authorizedGlideUrl(imageUrl)
    }

    private fun authorizedGlideUrl(url: String): GlideUrl = GlideUrl(
        url,
        remoteLibraryUseCase.authHeader?.let { authHeader ->
            LazyHeaders.Builder()
                .addHeader(authHeader.first, authHeader.second)
                .build()
        }
    )

}
