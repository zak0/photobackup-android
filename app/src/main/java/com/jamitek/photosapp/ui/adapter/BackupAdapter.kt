package com.jamitek.photosapp.ui.adapter

import android.content.Context
import com.jamitek.photosapp.R
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.backup.BackupSettingItemKey
import com.jamitek.photosapp.ui.viewmodel.BackupViewModel

class BackupAdapter(private val viewModel: BackupViewModel) : SettingsAdapter() {

    override val items = viewModel.items.value ?: emptyList()

    override fun getItemTitle(itemKey: SettingsItemKey, context: Context): String = context.getString(
        when (itemKey as BackupSettingItemKey) {
            BackupSettingItemKey.SECTION_TITLE_BACKUP_STATUS -> R.string.backupSectionTitleBackupStatus
            BackupSettingItemKey.ITEM_PHOTOS_STATUS -> R.string.backupPhotosStatus
            BackupSettingItemKey.ITEM_BACKUP_STATUS -> R.string.backupBackupStatus
            BackupSettingItemKey.ITEM_CAMERA_DIR -> R.string.backupCameraDir
            BackupSettingItemKey.SECTION_TITLE_CONNECTION_STATUS -> R.string.backupSectionTitleConnectionStatus
            BackupSettingItemKey.ITEM_CONNECTION_STATUS -> R.string.backupConnectionStatus
            BackupSettingItemKey.ITEM_SERVER_DETAILS -> R.string.backupServerDetails
            BackupSettingItemKey.SECTION_TITLE_SERVER_ADMIN -> R.string.backupSectionTitleServerAdmin
            BackupSettingItemKey.ITEM_RESCAN_LIBRARY -> R.string.backupRescanLibrary
        }
    )

    override fun onItemClicked(key: SettingsItemKey) = viewModel.onItemClicked(key as BackupSettingItemKey)

}