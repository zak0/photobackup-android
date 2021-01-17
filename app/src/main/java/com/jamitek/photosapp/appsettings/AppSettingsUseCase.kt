package com.jamitek.photosapp.appsettings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.jamitek.photosapp.Event
import com.jamitek.photosapp.SettingsItem
import com.jamitek.photosapp.locallibrary.LocalCameraRepository
import com.jamitek.photosapp.api.ServerConfigRepository
import com.jamitek.photosapp.remotelibrary.RemoteLibraryAdminRepository
import com.jamitek.photosapp.ui.AppSettingsScreenEvent

class AppSettingsUseCase(
    private val serverConfigRepository: ServerConfigRepository,
    private val serverAdminRepository: RemoteLibraryAdminRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val cameraRepository: LocalCameraRepository
) {

    private val mutableUiEvent = MutableLiveData<Event<AppSettingsScreenEvent?>>()
    val uiEvent: LiveData<Event<AppSettingsScreenEvent?>> = mutableUiEvent

    val settingItems: LiveData<List<SettingsItem>> = MediatorLiveData<List<SettingsItem>>().apply {
        addSource(cameraRepository.status) { value = buildSettings() }
        addSource(serverAdminRepository.libraryScanStatus) { value = buildSettings() }
        value = buildSettings()
    }

    fun onItemClicked(key: AppSettingsSettingItemKey) {
        when (key) {
            AppSettingsSettingItemKey.ITEM_CAMERA_DIR -> emitUiEvent(AppSettingsScreenEvent.ShowCameraDirSelection)

            AppSettingsSettingItemKey.ITEM_SERVER_DETAILS -> emitUiEvent(AppSettingsScreenEvent.ShowServerSetup)

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

    private fun emitUiEvent(event: AppSettingsScreenEvent) {
        mutableUiEvent.value = Event(event)
    }

    private fun buildSettings(): List<SettingsItem> {
        return listOf(
            SettingsItem(AppSettingsSettingItemKey.SECTION_TITLE_BACKUP),
            SettingsItem(AppSettingsSettingItemKey.ITEM_CAMERA_DIR, value = {
                cameraRepository.cameraDirUriString?.let {
                    "Tap to change backup source"
                } ?: "Backup source not set"
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