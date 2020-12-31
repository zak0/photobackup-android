package com.jamitek.photosapp.appsettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.locallibrary.LocalCameraRepository
import com.jamitek.photosapp.api.ServerConfigRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository
import com.jamitek.photosapp.ui.BackupScreenEvent

class AppSettingsUseCase(
    private val serverConfigRepository: ServerConfigRepository,
    private val serverAdminRepository: RemoteLibraryAdminRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val cameraRepository: LocalCameraRepository
) {

    private val mutableUiEvent = MutableLiveData<Event<BackupScreenEvent?>>()
    val uiEvent: LiveData<Event<BackupScreenEvent?>> = mutableUiEvent

    val settingItems: LiveData<List<SettingsItem>> = MediatorLiveData<List<SettingsItem>>().apply {
        addSource(cameraRepository.status) { value = buildSettings() }
        addSource(serverAdminRepository.libraryScanStatus) { value = buildSettings() }
        value = buildSettings()
    }

    fun onItemClicked(key: AppSettingsSettingItemKey) {
        when (key) {
            AppSettingsSettingItemKey.ITEM_PHOTOS_STATUS -> emitUiEvent(BackupScreenEvent.StartBackupWorker)
            AppSettingsSettingItemKey.ITEM_CAMERA_DIR -> emitUiEvent(BackupScreenEvent.ShowCameraDirSelection)

            AppSettingsSettingItemKey.ITEM_SERVER_DETAILS -> emitUiEvent(BackupScreenEvent.ShowServerSetup)

            AppSettingsSettingItemKey.ITEM_RESCAN_LIBRARY -> serverAdminRepository.initLibraryScan(
                refreshStatusUntilDone = true
            )

            else -> Unit //error("No click handler for $key")
        }
    }

    fun onItemToggled(key: AppSettingsSettingItemKey, isChecked: Boolean) {
        when (key) {
            AppSettingsSettingItemKey.ITEM_BACKUP_PHOTOS_TOGGLE -> {
                appSettingsRepository.backupPhotos = isChecked
            }

            AppSettingsSettingItemKey.ITEM_BACKUP_VIDEOS_TOGGLE -> {
                appSettingsRepository.backupVideos = isChecked
            }

            else -> Unit
        }
    }

    private fun emitUiEvent(event: BackupScreenEvent) {
        mutableUiEvent.value = Event(event)
    }

    private fun buildSettings(): List<SettingsItem> {
        return listOf(
            SettingsItem(AppSettingsSettingItemKey.SECTION_TITLE_BACKUP_STATUS),
            SettingsItem(AppSettingsSettingItemKey.ITEM_PHOTOS_STATUS, value = {
                "${cameraRepository.status.value?.localFilesCount ?: -1} photos/videos - Tap to rescan & backup"
            }),
            SettingsItem(AppSettingsSettingItemKey.ITEM_BACKUP_STATUS, value = {
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
            }),
            SettingsItem(AppSettingsSettingItemKey.ITEM_CAMERA_DIR, value = {
                cameraRepository.cameraDirUriString?.let {
                    "Tap to change"
                } ?: "Tap to set"
            }),
            SettingsItem(
                AppSettingsSettingItemKey.ITEM_BACKUP_PHOTOS_TOGGLE,
                value = {
                    if (appSettingsRepository.backupPhotos) "Photos will be backed up"
                    else "Photos will not be backed up"
                },
                isToggled = { appSettingsRepository.backupPhotos }
            ),
            SettingsItem(
                AppSettingsSettingItemKey.ITEM_BACKUP_VIDEOS_TOGGLE,
                value = {
                    if (appSettingsRepository.backupVideos) "Videos will be backed up"
                    else "Videos will not be backed up"
                },
                isToggled = { appSettingsRepository.backupVideos }
            ),
            SettingsItem(AppSettingsSettingItemKey.SECTION_TITLE_CONNECTION_STATUS),
            SettingsItem(AppSettingsSettingItemKey.ITEM_CONNECTION_STATUS),
            SettingsItem(AppSettingsSettingItemKey.ITEM_SERVER_DETAILS, value = {
                serverConfigRepository.baseUrl.takeUnless { it.isEmpty() } ?: "Tap to set"
            }),
            SettingsItem(AppSettingsSettingItemKey.SECTION_TITLE_SERVER_ADMIN),
            SettingsItem(AppSettingsSettingItemKey.ITEM_RESCAN_LIBRARY, value = {
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
            })
        )
    }

}