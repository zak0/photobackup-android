package com.jamitek.photosapp.remotelibrary

import com.jamitek.photosapp.api.ServerConfigRepository
import com.jamitek.photosapp.locallibrary.LocalCameraRepository

class MediaTimelineUseCase(
    private val serverConfigRepo: ServerConfigRepository,
    private val remoteLibraryRepo: RemoteLibraryRepository,
    private val localCameraRepo: LocalCameraRepository
) {

    val urlIsSet
        get() = serverConfigRepo.urlIsSet

    /** Authorization header is used for fetching files with Glide on View layer */
    val authHeader
        get() = serverConfigRepo.authHeader

    val allMedia = remoteLibraryRepo.allMedia

    val lastBackupTimestamp = localCameraRepo.lastBackupTime

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