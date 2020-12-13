package com.jamitek.photosapp.remotelibrary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.model.RemoteLibraryScanStatus
import com.jamitek.photosapp.api.ApiClient
import kotlinx.coroutines.*

class RemoteLibraryAdminRepository(private val libraryApi: ApiClient) {

    private companion object {
        const val TAG = "AdminRepo"
    }

    private val mutableLibraryScanStatus =
        MutableLiveData<RemoteLibraryScanStatus>().apply { value = null }
    val libraryScanStatus: LiveData<RemoteLibraryScanStatus> = mutableLibraryScanStatus

    fun initLibraryScan(refreshStatusUntilDone: Boolean = false) {
        GlobalScope.launch {
            val startSuccess = libraryApi.initRemoteLibraryScan().data == true
            if (startSuccess) {
                refreshScanStatus(refreshStatusUntilDone)
            }
        }
    }

    fun refreshScanStatus(refreshUntilDone: Boolean = false) {
        GlobalScope.launch {
            libraryApi.getRemoteLibraryScanStatus().data?.also { scanStatus ->
                withContext(Dispatchers.Main) {
                    mutableLibraryScanStatus.value = scanStatus
                }

                if (refreshUntilDone && scanStatus.state != "DONE") {
                    delay(1000)
                    refreshScanStatus(refreshUntilDone = true)
                }
            }
        }
    }

}