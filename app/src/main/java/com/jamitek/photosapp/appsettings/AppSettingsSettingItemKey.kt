package com.jamitek.photosapp.appsettings

import com.jamitek.photosapp.SettingsItemKey

enum class AppSettingsSettingItemKey(
    override val isTitle: Boolean = false,
    override val isToggleable: Boolean = false
) : SettingsItemKey {
    SECTION_TITLE_CONNECTION_STATUS(isTitle = true),
    ITEM_CONNECTION_STATUS,
    ITEM_SERVER_DETAILS,

    SECTION_TITLE_BACKUP(isTitle = true),
    ITEM_PHOTOS_STATUS,
    ITEM_BACKUP_STATUS,
    ITEM_CAMERA_DIR,
    ITEM_BACKUP_PHOTOS_TOGGLE(isToggleable = true),
    ITEM_BACKUP_VIDEOS_TOGGLE(isToggleable = true),

    SECTION_TITLE_SERVER_ADMIN(isTitle = true),
    ITEM_RESCAN_LIBRARY
}