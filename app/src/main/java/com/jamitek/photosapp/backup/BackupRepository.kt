package com.jamitek.photosapp.backup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class BackupRepository {

    private val mutableSettings = MutableLiveData<List<BackupSettingItem>>(emptyList())
    val settingItems: LiveData<List<BackupSettingItem>> = mutableSettings

    init {
        buildSettings()
    }

    private fun buildSettings() {
        mutableSettings.value = listOf(
            BackupSettingItem(BackupSettingItemKey.SECTION_TITLE_CONNECTION_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_CONNECTION_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_SERVER_DETAILS),
            BackupSettingItem(BackupSettingItemKey.SECTION_TITLE_BACKUP_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_PHOTOS_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_BACKUP_STATUS),
            BackupSettingItem(BackupSettingItemKey.ITEM_CAMERA_DIR)
        )
    }

}