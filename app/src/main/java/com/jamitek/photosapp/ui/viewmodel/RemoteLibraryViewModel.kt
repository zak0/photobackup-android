package com.jamitek.photosapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.load.model.GlideUrl
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.api.ServerConfigRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryRepository

class RemoteLibraryViewModel(
    private val serverConfigRepository: ServerConfigRepository,
    private val repository: RemoteLibraryRepository
) : ViewModel() {

    /**
     * Flag telling if a (seemingly) valid URL is set
     */
    val urlIsSet
        get() = serverConfigRepository.urlIsSet

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
        if (urlIsSet) {
            repository.fetchRemotePhotos()
        }
    }

    fun authorizedThumbnailGlideUrl(mediIdOnServer: Int): GlideUrl {
        val thumbUrl = serverConfigRepository.thumbnailUrl(mediIdOnServer)
        return serverConfigRepository.authorizedGlideUrl(thumbUrl)
    }

    fun authorizedImageGlideUrl(mediaIdOnServer: Int): GlideUrl {
        val imageUrl = serverConfigRepository.mediaUrl(mediaIdOnServer)
        return serverConfigRepository.authorizedGlideUrl(imageUrl)
    }

    fun mediaUri(mediaIdOnServer: Int): Uri {
        return Uri.parse(serverConfigRepository.mediaUrl(mediaIdOnServer))
    }

    fun mediaUriHeaders(): Map<String, String> {
        return mapOf(serverConfigRepository.authHeader)
    }

}
