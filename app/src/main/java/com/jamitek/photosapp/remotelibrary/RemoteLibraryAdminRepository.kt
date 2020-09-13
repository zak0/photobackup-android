package com.jamitek.photosapp.remotelibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.model.RemoteLibraryScanStatus
import com.jamitek.photosapp.networking.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RemoteLibraryAdminRepository(private val libraryApi: ApiClient) {

    private val mutableLibraryScanStatus =
        MutableLiveData<RemoteLibraryScanStatus>().apply { value = null }
    val libraryScanStatus: LiveData<RemoteLibraryScanStatus> = mutableLibraryScanStatus

    fun initLibraryScan() {
        GlobalScope.launch {
            val startSuccess = libraryApi.initRemoteLibraryScan().data == true
            if (startSuccess) {
                refreshScanStatus()
            }
        }
    }

    fun refreshScanStatus() {
        GlobalScope.launch {
            libraryApi.getRemoteLibraryScanStatus().data?.also { scanStatus ->
                withContext(Dispatchers.Main) {
                    mutableLibraryScanStatus.value = scanStatus
                }
            }
        }
    }

}