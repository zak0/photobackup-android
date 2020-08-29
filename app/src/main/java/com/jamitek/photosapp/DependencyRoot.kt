package com.jamitek.photosapp

import android.app.Application
import com.jamitek.photosapp.database.KeyValueStore
import com.jamitek.photosapp.networking.ApiClient
import com.jamitek.photosapp.networking.PhotosSerializer
import com.jamitek.photosapp.networking.ResponseParser

class DependencyRoot(app: Application) {

    val keyValueStore by lazy { KeyValueStore(app) }
    private val photosSerializer by lazy { PhotosSerializer() }
    private val apiResponseParser by lazy { ResponseParser() }
    private val apiClient by lazy { ApiClient(photosSerializer, apiResponseParser) }
    val remoteLibraryRepository by lazy { RemoteLibraryRepository(apiClient) }

}