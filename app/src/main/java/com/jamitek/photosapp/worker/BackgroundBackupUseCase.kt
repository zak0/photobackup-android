package com.jamitek.photosapp.worker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jamitek.photosapp.appsettings.AppSettingsRepository
import com.jamitek.photosapp.locallibrary.LocalCameraLibraryStatus
import com.jamitek.photosapp.locallibrary.LocalCameraRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackgroundBackupUseCase(
    private val cameraRepository: LocalCameraRepository,
    private val appSettingsRepository: AppSettingsRepository
) {

    private companion object {
        const val TAG = "BackgroundBuUseCase"
    }

    private val mutableWorkStatus = MediatorLiveData<WorkStatus>().apply {
        addSource(cameraRepository.status) {
            // TODO Use the received status to update progress of upload
            //  (counts of all and not-backed-up files are in it).
            handleWorkStatusTransition(to = it.asWorkStatus())
        }

        // Below is initial status
        CoroutineScope(Dispatchers.Main).launch {
            value = cameraRepository.status.value.asWorkStatus()
        }
    }

    val workStatus: LiveData<WorkStatus> = mutableWorkStatus

    val completionNotificationMessage: String
        get() = cameraRepository.status.value?.let {
            if (it.waitingForBackupCount == 0) {
                "Done. All backed up!"
            } else {
                "Done with error: ${it.waitingForBackupCount} files not backed up."
            }
        } ?: "Done with error: unable to read library state."

    /**
     * Initiates a background process of scanning and uploading of media files.
     *
     * [LocalCameraRepository] should already take care of not allowing for
     * starting a new scan/upload if there is one already running. I.e. no
     * need to enforce this here.
     */
    fun scanAndBackup() {
        cameraRepository.scanAndBackup(
            uploadPhotos = appSettingsRepository.backupPhotos,
            uploadVideos = appSettingsRepository.backupVideos
        )
    }

    private fun handleWorkStatusTransition(to: WorkStatus) {
        val newStatus: WorkStatus? =
            if (to == WorkStatus.Idle) {
                when (workStatus.value) {
                    WorkStatus.Uploading -> {
                        // Transition to Idle means that the worker is done if previous
                        // state was Uploading
                        WorkStatus.Done
                    }

                    WorkStatus.Scanning -> {
                        // Transition to Idle means that we can now start uploading if
                        // previous state was Scanning
                        //
                        // No need to emit a new state, repository should do it once
                        // upload is started. (Even if there's nothing to upload, it should
                        // still start the upload process, and reflect this in its status.)
                        null
                    }

                    WorkStatus.Done -> {
                        // If previous status was Done, we're okay transitioning back to Idle.
                        WorkStatus.Idle
                    }

                    else -> {
                        // This is an unknown transition, and should not happen.
                        null
                    }
                }

            } else {
                // Otherwise we're good with just the new state
                to
            }


        newStatus?.also {
            // Only emit new state if it changed from previous
            val previousStatus = workStatus.value
            if (previousStatus != newStatus) {
                Log.d(TAG, "Transition to: $newStatus")
                CoroutineScope(Dispatchers.Main).launch {
                    mutableWorkStatus.value = it

                    // If we're now done, mark the worker to be Idle. Without this, logic for
                    // consecutive works will not work as expected because of status being
                    // immediately Done.
                    if (newStatus == WorkStatus.Done) {
                        handleWorkStatusTransition(WorkStatus.Idle)
                    }
                }
            }
        }
    }

}

private fun LocalCameraLibraryStatus?.asWorkStatus(): WorkStatus = this?.let {
    if (!it.isScanning && !it.isUploading) WorkStatus.Idle
    else if (it.isScanning) WorkStatus.Scanning
    else if (it.isUploading) WorkStatus.Uploading
    else WorkStatus.Unknown
} ?: WorkStatus.Unknown

enum class WorkStatus {
    Idle,
    Scanning,
    Uploading,
    Done,
    Unknown
}