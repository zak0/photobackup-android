package com.jamitek.photosapp

import android.app.Application
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.SqliteLocalMediaDb
import com.jamitek.photosapp.locallibrary.LocalLibraryRepository
import com.jamitek.photosapp.locallibrary.LocalLibraryScanner
import com.jamitek.photosapp.networking.ApiClient
import com.jamitek.photosapp.networking.MediaSerializer
import com.jamitek.photosapp.remotelibrary.RemoteLibraryRepository
import com.jamitek.photosapp.storage.StorageAccessHelper

/**
 * Pure DI dependency manager.
 *
 * This should be initialized only once per app process lifetime, so a good place to init (and
 * keep a reference) at it [Application.onCreate].
 */
class DependencyRoot(app: Application) {

    private val mediaSerializer by lazy { MediaSerializer() }
    private val localMediaDb by lazy { SqliteLocalMediaDb(app) }
    private val apiClient by lazy { ApiClient(mediaSerializer) }
    private val localLibraryScanner by lazy { LocalLibraryScanner(app) }
    private val storageAccessHelper by lazy { StorageAccessHelper(app) }

    val keyValueStore by lazy { KeyValueStore(app) }
    val remoteLibraryRepository by lazy { RemoteLibraryRepository(apiClient) }
    val localLibraryRepository by lazy {
        LocalLibraryRepository(
            keyValueStore,
            localMediaDb,
            localLibraryScanner,
            storageAccessHelper,
            apiClient
        )
    }

}