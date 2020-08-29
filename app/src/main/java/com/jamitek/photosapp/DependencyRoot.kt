package com.jamitek.photosapp

import android.app.Application
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.database.SqliteLocalMediaDb
import com.jamitek.photosapp.locallibrary.LocalLibraryRepository
import com.jamitek.photosapp.locallibrary.LocalLibraryScanner
import com.jamitek.photosapp.networking.ApiClient
import com.jamitek.photosapp.networking.PhotosSerializer
import com.jamitek.photosapp.networking.ResponseParser

/**
 * Pure DI dependency manager.
 *
 * This should be initialized only once per app process lifetime, so a good place to init (and
 * keep a reference) at it [Application.onCreate].
 */
class DependencyRoot(app: Application) {

    val keyValueStore by lazy { KeyValueStore(app) }
    private val photosSerializer by lazy { PhotosSerializer() }
    private val apiResponseParser by lazy { ResponseParser() }
    private val localMediaDb by lazy { SqliteLocalMediaDb(app) }
    private val apiClient by lazy { ApiClient(photosSerializer, apiResponseParser) }
    private val localLibraryScanner by lazy { LocalLibraryScanner(app) }
    val remoteLibraryRepository by lazy { RemoteLibraryRepository(apiClient) }
    val localLibraryRepository by lazy {
        LocalLibraryRepository(
            keyValueStore,
            localMediaDb,
            localLibraryScanner
        )
    }

}