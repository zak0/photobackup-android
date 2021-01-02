package com.jamitek.photosapp.remotelibrary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.model.RemoteMedia
import com.jamitek.photosapp.api.ApiClient
import com.jamitek.photosapp.util.DateUtil
import kotlinx.coroutines.*

class RemoteLibraryRepository(private val libraryApi: ApiClient) {

    companion object {
        private const val TAG = "RemoteLibraryRepository"
    }

    private val mutableAllUnsorted = MutableLiveData<List<RemoteMedia>>(emptyList())
    val allUnsorted: LiveData<List<RemoteMedia>> = mutableAllUnsorted

    fun fetchRemoteMediaMetas() {
        libraryApi.getAllMedia { success, media ->
            CoroutineScope(Dispatchers.Main).launch {
                if (success) {
                    mutableAllUnsorted.value = media
                } else {
                    Log.e(TAG, "Fetching remote media metas failed")
                }
            }
        }
    }

}
