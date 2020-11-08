package com.jamitek.photosapp.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.locallibrary.LocalCameraRepository
import com.jamitek.photosapp.networking.ServerConfigRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository

class BackupUseCase(
    private val serverConfigRepository: ServerConfigRepository,
    private val serverAdminRepository: RemoteLibraryAdminRepository,
    private val cameraRepository: LocalCameraRepository
) {

    private val mutableSettings = MutableLiveData<List<BackupSettingItem>>(emptyList())
    val settingItems: LiveData<List<BackupSettingItem>> = mutableSettings

    init {
        buildSettings()
    }

    fun onItemClicked(key: BackupSettingItemKey) {
        when (key) {
            BackupSettingItemKey.ITEM_PHOTOS_STATUS -> cameraRepository.scan()
            // TODO Add rest of the items
        }
    }

    private fun buildSettings() {
        mutableSettings.value = listOf(
            BackupSettingItem(BackupSettingItemKey.SECTION_TITLE_BACKUP_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_PHOTOS_STATUS) {
                "${cameraRepository.status.value?.localFilesCount ?: -1} (-1 GB) - Tap to rescan"
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