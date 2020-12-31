package com.jamitek.photosapp.ui.adapter

import android.content.Context
import com.jamitek.photosapp.R
import com.jamitek.photosapp.SettingsItemKey
import com.jamitek.photosapp.appsettings.AppSettingsSettingItemKey
import com.jamitek.photosapp.ui.viewmodel.AppSettingsViewModel

class BackupAdapter(private val viewModel: AppSettingsViewModel) : SettingsAdapter() {

    override val items = viewModel.items.value ?: emptyList()

    override fun getItemTitle(itemKey: SettingsItemKey, context: Context): String =
        context.getString(
            when (itemKey as AppSettingsSettingItemKey) {
                AppSettingsSettingItemKey.SECTION_TITLE_BACKUP_STATUS -> R.string.backupSectionTitleBackupStatus
                AppSettingsSettingItemKey.ITEM_PHOTOS_STATUS -> R.string.backupPhotosStatus
                AppSettingsSettingItemKey.ITEM_BACKUP_STATUS -> R.string.backupBackupStatus
                AppSettingsSettingItemKey.ITEM_CAMERA_DIR -> R.string.backupCameraDir
                AppSettingsSettingItemKey.ITEM_BACKUP_PHOTOS_TOGGLE -> R.string.backupPhotosToggle
                AppSettingsSettingItemKey.ITEM_BACKUP_VIDEOS_TOGGLE -> R.string.backupVideosToggle
                AppSettingsSettingItemKey.SECTION_TITLE_CONNECTION_STATUS -> R.string.backupSectionTitleConnectionStatus
                AppSettingsSettingItemKey.ITEM_CONNECTION_STATUS -> R.string.backupConnectionStatus
                AppSettingsSettingItemKey.ITEM_SERVER_DETAILS -> R.string.backupServerDetails
                AppSettingsSettingItemKey.SECTION_TITLE_SERVER_ADMIN -> R.string.backupSectionTitleServerAdmin
                AppSettingsSettingItemKey.ITEM_RESCAN_LIBRARY -> R.string.backupRescanLibrary
            }
        )

    override fun onItemClicked(key: SettingsItemKey) =
        viewModel.onItemClicked(key as AppSettingsSettingItemKey)

    override fun onItemToggled(key: SettingsItemKey, isChecked: Boolean) =
        viewModel.onItemToggled(key as AppSettingsSettingItemKey, isChecked)
}