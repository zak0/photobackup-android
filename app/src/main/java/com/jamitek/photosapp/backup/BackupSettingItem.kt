package com.jamitek.photosapp.backup

class BackupSettingItem(
    val key: BackupSettingItemKey,
    val value: () -> String = { "Not implemented" }
)