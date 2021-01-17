package com.jamitek.photosapp

import android.app.Application
import com.jamitek.photosapp.api.*
import com.jamitek.photosapp.appsettings.AppSettingsUseCase
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.SqliteLocalMediaDb
import com.jamitek.photosapp.locallibrary.LocalCameraRepository
import com.jamitek.photosapp.locallibrary.LocalLibraryScanner
import com.jamitek.photosapp.appsettings.AppSettingsRepository
import com.jamitek.photosapp.locallibrary.SetCameraDirUseCase
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository
import com.jamitek.photosapp.remotelibrary.TimelineUseCase
import com.jamitek.photosapp.remotelibrary.RemoteLibraryRepository
import com.jamitek.photosapp.storage.StorageAccessHelper
import com.jamitek.photosapp.worker.BackgroundBackupUseCase

/**
 * Pure DI dependency manager.
 *
 * This should be initialized only once per app process lifetime, so a good place to init (and
 * keep a reference) at it [Application.onCreate].
 */
class DependencyRoot(app: Application) {
    
    private val mediaSerializer by lazy { ApiSerializer() }
    private val localMediaDb by lazy { SqliteLocalMediaDb(app) }
    private val localLibraryScanner by lazy { LocalLibraryScanner(app) }
    private val storageAccessHelper by lazy { StorageAccessHelper(app) }
    private val serverConfigStore by lazy { ServerConfigStore(app) }
    private val keyValueStore by lazy { KeyValueStore(app) }
    private val apiClient by lazy { ApiClient(serverConfigStore, mediaSerializer) }

    private val serverConfigRepository by lazy { ServerConfigRepository(serverConfigStore) }
    private val remoteLibraryRepository by lazy { RemoteLibraryRepository(apiClient) }
    private val localCameraRepository by lazy {
        LocalCameraRepository(
            keyValueStore,
            localMediaDb,
            localLibraryScanner,
            storageAccessHelper,
            apiClient
        )
    }
    private val remoteLibraryAdminRepository by lazy { RemoteLibraryAdminRepository(apiClient) }
    private val appSettingsRepository by lazy { AppSettingsRepository(keyValueStore) }

    val setCameraDirUseCase by lazy { SetCameraDirUseCase(localCameraRepository) }
    val appSettingsUseCase by lazy {
        AppSettingsUseCase(
            serverConfigRepository,
            remoteLibraryAdminRepository,
            appSettingsRepository,
            localCameraRepository
        )
    }
    val serverConfigUseCase by lazy { ServerConfigUseCase(serverConfigRepository) }
    val backgroundBackupUseCase by lazy {
        BackgroundBackupUseCase(
            localCameraRepository,
            appSettingsRepository
        )
    }
    val timelineUseCase by lazy {
        TimelineUseCase(
            serverConfigRepository,
            remoteLibraryRepository,
            localCameraRepository
        )
    }

}