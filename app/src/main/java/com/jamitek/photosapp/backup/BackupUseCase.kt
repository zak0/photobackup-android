package com.jamitek.photosapp.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.SettingsItem
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

    private val initialSettings = MutableLiveData<List<SettingsItem>>(emptyList())
    val settingItems: LiveData<List<SettingsItem>> = MediatorLiveData<List<SettingsItem>>().apply {
        addSource(cameraRepository.status) { value = buildSettings() }
        addSource(serverAdminRepository.libraryScanStatus) { value = buildSettings() }
        addSource(initialSettings) { value = it}
    }

    fun onItemClicked(key: BackupSettingItemKey) {
        when (key) {
            BackupSettingItemKey.ITEM_PHOTOS_STATUS -> cameraRepository.scan()
            BackupSettingItemKey.ITEM_BACKUP_STATUS -> cameraRepository.backup() // TODO Do this after scan, if conditions for upload are met
            BackupSettingItemKey.ITEM_CAMERA_DIR -> emitUiEvent(BackupScreenEvent.ShowCameraDirSelection)

            BackupSettingItemKey.ITEM_SERVER_DETAILS -> emitUiEvent(BackupScreenEvent.ShowServerSetup)

            BackupSettingItemKey.ITEM_RESCAN_LIBRARY -> serverAdminRepository.initLibraryScan(refreshStatusUntilDone = true)

            else -> error("No click handler for $key")
        }
    }

    private fun emitUiEvent(event: BackupScreenEvent) {
        mutableUiEvent.value = Event(event)
    }

    private fun buildSettings(): List<SettingsItem> {
        return listOf(
            SettingsItem(BackupSettingItemKey.SECTION_TITLE_BACKUP_STATUS),
            SettingsItem(BackupSettingItemKey.ITEM_PHOTOS_STATUS) {
                "${cameraRepository.status.value?.localFilesCount ?: -1} photos - Tap to rescan"
            },
            SettingsItem(BackupSettingItemKey.ITEM_BACKUP_STATUS) {
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
            SettingsItem(BackupSettingItemKey.ITEM_CAMERA_DIR) {
                cameraRepository.cameraDirUriString?.let {
                    "Tap to change"
                } ?: "Tap to set"
            },
            SettingsItem(BackupSettingItemKey.SECTION_TITLE_CONNECTION_STATUS),
            SettingsItem(BackupSettingItemKey.ITEM_CONNECTION_STATUS),
            SettingsItem(BackupSettingItemKey.ITEM_SERVER_DETAILS) {
                serverConfigRepository.baseUrl.takeUnless { it.isEmpty() } ?: "Tap to set"
            },
            SettingsItem(BackupSettingItemKey.SECTION_TITLE_SERVER_ADMIN),
            SettingsItem(BackupSettingItemKey.ITEM_RESCAN_LIBRARY) {
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