package com.jamitek.photosapp.backup

import com.jamitek.photosapp.SettingsItemKey

enum class BackupSettingItemKey(override val isTitle: Boolean = false) : SettingsItemKey {
    SECTION_TITLE_CONNECTION_STATUS(true),
    ITEM_CONNECTION_STATUS,
    ITEM_SERVER_DETAILS,

    SECTION_TITLE_BACKUP_STATUS(true),
    ITEM_PHOTOS_STATUS,
    ITEM_BACKUP_STATUS,
    ITEM_CAMERA_DIR,

    SECTION_TITLE_SERVER_ADMIN(true),
    ITEM_RESCAN_LIBRARY
}