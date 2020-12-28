package com.jamitek.photosapp.remotelibrary

import com.jamitek.photosapp.api.ServerConfigRepository

class RemoteLibraryBrowserUseCase(
    private val serverConfigRepo: ServerConfigRepository,
    private val remoteLibraryRepo: RemoteLibraryRepository
) {

    val urlIsSet
        get() = serverConfigRepo.urlIsSet

    /** Authorization header is used for fetching files with Glide on View layer */
    val authHeader
        get() = serverConfigRepo.authHeader

    val allMedia = remoteLibraryRepo.allMedia

    /**
     * Media grouped into nicely displayable sets.
     *
     * TODO Instead of hardcoding, use [RemoteLibraryRepository.mediaPerMonth] or
     *  [RemoteLibraryRepository.mediaPerDate] per user's settings.
     */
    val groupedMedia = remoteLibraryRepo.mediaPerMonth

    fun refreshRemotePhotos() {
        if (urlIsSet) {
            remoteLibraryRepo.fetchRemotePhotos()
        }
    }

    fun thumbnailUrl(mediaIdOnServer: Int) = serverConfigRepo.thumbnailUrl(mediaIdOnServer)
    fun mediaUrl(mediaIdOnServer: Int) = serverConfigRepo.mediaUrl(mediaIdOnServer)

}