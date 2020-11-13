package com.jamitek.photosapp.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.locallibrary.LocalCameraRepository
import com.jamitek.photosapp.networking.ServerConfigRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository
import com.jamitek.photosapp.ui.BackupScreenEvent

class BackupUseCase(
    private val serverConfigRepository: ServerConfigRepository,
    private val serverAdminRepository: RemoteLibraryAdminRepository,
    private val cameraRepository: LocalCameraRepository
) {

    private val mutableUiEvent = MutableLiveData<Event<BackupScreenEvent?>>()
    val uiEvent: LiveData<Event<BackupScreenEvent?>> = mutableUiEvent

    private val initialSettings = MutableLiveData<List<BackupSettingItem>>(emptyList())
    val settingItems: LiveData<List<BackupSettingItem>> = MediatorLiveData<List<BackupSettingItem>>().apply {
        addSource(cameraRepository.status) { value = buildSettings() }
        addSource(serverAdminRepository.libraryScanStatus) { value = buildSettings() }
        addSource(initialSettings) { value = it}
    }

    init {
        initialSettings.value = buildSettings()
    }

    fun onItemClicked(key: BackupSettingItemKey) {
        when (key) {
            BackupSettingItemKey.ITEM_PHOTOS_STATUS -> cameraRepository.scan()
            BackupSettingItemKey.ITEM_BACKUP_STATUS -> cameraRepository.backup() // TODO Do this after scan, if conditions for upload are met
            BackupSettingItemKey.ITEM_CAMERA_DIR -> emitUiEvent(BackupScreenEvent.ShowCameraDirSelection)

            BackupSettingItemKey.ITEM_SERVER_DETAILS -> emitUiEvent(BackupScreenEvent.ShowServerSetup)

            BackupSettingItemKey.ITEM_RESCAN_LIBRARY -> serverAdminRepository.initLibraryScan(refreshStatusUntilDone = true)
        }
    }

    private fun emitUiEvent(event: BackupScreenEvent) {
        mutableUiEvent.value = Event(event)
    }

    private fun buildSettings(): List<BackupSettingItem> {
        return listOf(
            BackupSettingItem(BackupSettingItemKey.SECTION_TITLE_BACKUP_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_PHOTOS_STATUS) {
                "${cameraRepository.status.value?.localFilesCount ?: -1} photos - Tap to rescan"
            },
            BackupSettingItem(BackupSettingItemKey.ITEM_BACKUP_STATUS) {
                cameraRepository.status.value?.let { status ->
                    when {
                        status.isScanning -> "Scanning local files..."
                        status.isUploading -> "Uploading... ${status.waitingForBackupCount} files pending"
                        else -> {
                            if (status.waitingForBackupCount > 0) "${status.waitingForBackupCount} files pending upload"
                            else "All backed up"
                        }
                    }
                } ?: "Error - Unable to read status"
            },
            BackupSettingItem(BackupSettingItemKey.ITEM_CAMERA_DIR) {
                cameraRepository.cameraDirUriString?.let {
                    "Tap to change"
                } ?: "Tap to set"
            },
            BackupSettingItem(BackupSettingItemKey.SECTION_TITLE_CONNECTION_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_CONNECTION_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_SERVER_DETAILS) {
                serverConfigRepository.baseUrl.takeUnless { it.isEmpty() } ?: "Tap to set"
            },
            BackupSettingItem(BackupSettingItemKey.SECTION_TITLE_SERVER_ADMIN),
            BackupSettingItem(BackupSettingItemKey.ITEM_RESCAN_LIBRARY) {
                serverAdminRepository.libraryScanStatus.value?.let {
                    """
                        Server scan state: ${it.state}
                        mediaFilesDetected: ${it.mediaFilesDetected}
                        filesMoved: ${it.filesMoved}
                        filesRemoved: ${it.filesRemoved}
                        newFiles: ${it.newFiles}
                        filesToProcess: ${it.filesToProcess}
                        filesProcessed: ${it.filesProcessed}
                    """.trimIndent()
                } ?: "Server scan state: UNKNOWN"
            }
        )
    }

}