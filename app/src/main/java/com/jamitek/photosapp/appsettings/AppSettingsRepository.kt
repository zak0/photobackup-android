package com.jamitek.photosapp.appsettings

import com.jamitek.photosapp.database.KeyValueStore

class AppSettingsRepository(
    private val keyValueStore: KeyValueStore
) {

    private companion object {
        const val KEY_BACKUP_PHOTOS = "app.settings.backup.photos"
        const val KEY_BACKUP_VIDEOS = "app.settings.backup.videos"
    }

    var backupPhotos: Boolean
        get() = keyValueStore.getBoolean(KEY_BACKUP_PHOTOS, true)
        set(value) = keyValueStore.putBoolean(KEY_BACKUP_PHOTOS, value)

    var backupVideos: Boolean
        get() = keyValueStore.getBoolean(KEY_BACKUP_VIDEOS, true)
        set(value) = keyValueStore.putBoolean(KEY_BACKUP_VIDEOS, value)

}
